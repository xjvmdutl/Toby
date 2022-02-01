package sqlservice;

import dao.UserDao;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import sqlservice.jaxb.SqlType;
import sqlservice.jaxb.Sqlmap;

public class XmlSqlService implements SqlService{
    //매번 요청이 들어올떄마다 SQL을 읽어오는것은 비효율 적이므로 초기 한번 생성자를 통해 값을 읽은뒤 세팅한다
    private Map<String, String> sqlMap = new HashMap<>(); //읽어온 SQL 저장

    public XmlSqlService(){
        String contextPath = Sqlmap.class.getPackage().getName();
        try{
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
            for(SqlType sql : sqlmap.getSql()){
                sqlMap.put(sql.getKey(), sql.getValue()); //읽어온 SQL을 맵으로 저장
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e); //JAXBContext는 복구 불가능한 예외이므로 불필요한 throws를 피하도록 런타임 예외로 포장해서 던진다
        }
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if(sql == null){
            throw new SqlRetrievalFailureException(key);
        }else{
            return sql;
        }
    }
}
