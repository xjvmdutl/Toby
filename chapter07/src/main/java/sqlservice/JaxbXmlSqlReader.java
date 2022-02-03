package sqlservice;

import dao.UserDao;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import sqlservice.jaxb.SqlType;
import sqlservice.jaxb.Sqlmap;

public class JaxbXmlSqlReader implements SqlReader{
    private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";

    private String sqlmapFile = DEFAULT_SQLMAP_FILE;
    
    public void setSqlmapFile(String sqlmapFile) { //sqlMapFile은 sqlReader구현의 일부가 된다
        this.sqlmapFile = sqlmapFile; //해당 프로퍼티를 사용하면 프로퍼티에 주입된 값이 사용되고 사용하지 않는다면 기본값이 사용된다
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
