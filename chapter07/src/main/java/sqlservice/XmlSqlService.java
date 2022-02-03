package sqlservice;

import dao.UserDao;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import sqlservice.jaxb.SqlType;
import sqlservice.jaxb.Sqlmap;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {

    private SqlReader sqlReader;
    private SqlRegistry sqlRegistry; //의존 오브젝트를 DI 받도록 인터페이스 타입의 프로퍼티 선언

    public void setSqlReader(SqlReader sqlReader) {
        this.sqlReader = sqlReader;
    }

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }



    /*
    private Map<String, String> sqlMap = new HashMap<>(); //읽어온 SQL 저장
    private String sqlmapFile;

    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }
    */
    /*
    public XmlSqlService(){
        */

    /**
     * 생성자에서 예외가 발생할 수 있는 복잡한 코드를 다루는것은 좋지 않다 매번 요청이 들어올떄마다 SQL을 읽어오는것은 비효율 적이므로 초기 한번 생성자를 통해 값을
     * 읽은뒤 세팅한다 생성자에서 발생하는 예외는 다루기 힘들고, 상속하기 불편하고, 보안에도 문제가 생길 수 있다 초기상태를 가진 오브젝트를 만들고 별도의 초기화 메소드를
     * 사용하는 방법이 바람직하다
     *//*
        String contextPath = Sqlmap.class.getPackage().getName();
        try{
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");//읽어들일 파일의 위치와 이름이 코드에 고정되어있다(외부에서 DI 받는것이 좋다)
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
            for(SqlType sql : sqlmap.getSql()){
                sqlMap.put(sql.getKey(), sql.getValue()); //읽어온 SQL을 맵으로 저장
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e); //JAXBContext는 복구 불가능한 예외이므로 불필요한 throws를 피하도록 런타임 예외로 포장해서 던진다
        }
    }*/
    @PostConstruct //오브젝트를 생성하고 빈을 초기화 메소드로 지정
    public void loadSql() {
        //해당 코드의 제어권이 우리한테 있다면 생성 시점에 호출해 주면 되지만 제어권이 스프링에 있다.
        //스프링은 빈을 생성하고 주입한뒤, 초기화 메소드를 호출해주는 기능을 가지고 있다.
        //AOP를 위한 프록시 자동생성기가 대표적인 비 후처리기인데 이를 애노테이션을 이용한 빈 후처리기도 존재한다.
        //contexxt:annotaion-config 태그를 만들어 설정파일에 넣으면 빈 설정 기능에 사용할 수 있는 특별한 애노테이션 기능을 부여햐주는 빈 후처리기들이 등록된다
        //@PostConstruct 어노테이션을 이용해 빈 오브젝트의 초기화 메소드를 지정하는데 사용한다.
        /*
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
            for (SqlType sql : sqlmap.getSql()) {
                sqlMap.put(sql.getKey(), sql.getValue());
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
         */
        this.sqlReader.read(this.sqlRegistry);
    }
    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        /*
        String sql = sqlMap.get(key);
        if (sql == null) {
            throw new SqlRetrievalFailureException(key);
        } else {
            return sql;
        }
         */
        try{
            return this.sqlRegistry.findSql(key);
        }catch (SqlNotFoundException e){
            throw new SqlRetrievalFailureException(key);
        }
    }
    // --------- SqlRegistry ------------
    private Map<String, String> sqlMap = new HashMap<>(); //SqlRegistry 구현의 일부가 된다// 외부에서 접근 불가능

    @Override
    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if (sql == null) {
            throw new SqlNotFoundException(sql);
        } else {
            return sql;
        }
    }
    // --------- SQLReader ------------
    private String sqlmapFile;
    public void setSqlmapFile(String sqlmapFile) { //sqlMapFile은 sqlReader구현의 일부가 된다
        this.sqlmapFile = sqlmapFile;
    }
    @Override
    public void read(SqlRegistry sqlRegistry) {// 초기화를 위해 무엇을 할 것인가와 SQL을 어떻게 읽는지를 분리
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
            for (SqlType sql : sqlmap.getSql()) {
                sqlRegistry.registerSql(sql.getKey(), sql.getValue());
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
