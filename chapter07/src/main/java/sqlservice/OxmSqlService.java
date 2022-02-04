package sqlservice;

import dao.UserDao;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;
import sqlservice.jaxb.SqlType;
import sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {

    //Final로 선언하고 직접 값을 생성하기 때문에 DI하거나 변경 불가능
    //DI를 해줄것이 많이 있기 떄문에 단순한 디폴트 방식으로 제공해 줄 수 없다.
    //이런경우 하나의 빈 설정만으로 SqlService, SqlReader의 필요한 프로퍼티 설정이 모두 가능하도록 만들 필요가 있다
    //private로 외부에 노출되지 않으므로 OxmSqlService의 공개된 프로퍼티를 통해 간접적으로 DI 받아야한다
    private final OxmSqlReader oxmSqlReader = new OxmSqlReader(); //final이므로 변경 불가능, OXMSqlService와  OxmSqlReader는 강하게 결합되서 하나의 빈으로 등록되고 한번에 설정 가능

    private SqlRegistry sqlRegistry = new HashMapSqlRegistry(); // OXMSqlReader와 달리 단지 디폴트 오브젝트로 만들어진 프로퍼티로 필요에 따라 DI로 교체 가능

    private final BaseSqlService baseSqlService = new BaseSqlService(); //sqlService의 실제 구현 부분을 위임할 대상인 BaseSqlService를 인스턴스 변수로 정의

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    /*
    public void setSqlmapFile(String sqlmapFile) {
        this.oxmSqlReader.setSqlmapFile(sqlmapFile);
    }
    */
    public void setSqlmap(Resource sqlmap) {
        this.oxmSqlReader.setSqlmap(sqlmap);
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.oxmSqlReader.setUnmarshaller(unmarshaller);
    }

    @PostConstruct
    public void loadSql() {
        this.baseSqlService.setSqlReader(this.oxmSqlReader); //OXMService 프로퍼티를 통해 초기화된 속성을 작업대상인 baseSql로 위임하여 실제 작업은 baseSqlService가 동작하도록 한다
        this.baseSqlService.setSqlRegistry(this.sqlRegistry); 
        
        this.baseSqlService.loadSql(); //SQL 등록 초기화 작업을 위임
    }
    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        return this.baseSqlService.getSql(key);
    }

    private class OxmSqlReader implements SqlReader {//private 멤버클래스로 정의하여 OxmSqlService만이 사용 가능
        private Unmarshaller unmarshaller;
        //private final static String DEFAULT_SQLMAP_FILE= "sqlmap.xml";
        //private String sqlmapFile = DEFAULT_SQLMAP_FILE;
        private Resource sqlmap = new ClassPathResource("sqlmap.xml", UserDao.class); //해당 리소스에 대해 접근할수 있는 추상화된 핸들러 역할이므로 실제 리소스는 아니다.
        //따라서 해당 오브젝트가 만들어 졌다고 해서 실제 리소스가 존재하지 않을 수도 있다.

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }


        /*
        public void setSqlmapFile(String sqlmapFile) {
            this.sqlmapFile = sqlmapFile;
        }
        */
        public void setSqlmap(Resource sqlmap) {
            this.sqlmap = sqlmap;
        }
        @Override
        public void read(SqlRegistry sqlRegistry) {
            try {
                Source source = new StreamSource(
                   // UserDao.class.getResourceAsStream(this.sqlmapFile)
                    sqlmap.getInputStream()//Resource 종류에 상관없이 Stream으로 가지고 올 수 있다
                );
                Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);
                for(SqlType sql : sqlmap.getSql()){
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }
            }catch (IOException e){
                throw new IllegalArgumentException(sqlmap.getFilename());
            }
        }

    }



}
