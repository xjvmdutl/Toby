package sqlservice.jaxb;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;

public class JaxbTest {

    @Test
    public void readSqlmap() throws JAXBException {
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext
            .newInstance(contextPath);//바인딩용 클래스 위치를 가지고 JAXB 컨텍스트를 만든다.
        Unmarshaller unmarshaller = context.createUnmarshaller();
        System.out.println(JaxbTest.class.getResourceAsStream("/sqlservice/jaxb/sqlmap.xml"));
        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(
            JaxbTest.class.getResourceAsStream("/sqlservice/jaxb/sqlmap.xml")
        );
        List<SqlType> sqlList = sqlmap.getSql();

        assertThat(sqlList.size(), is(3));
        assertThat(sqlList.get(0).getKey(), is("add"));
        assertThat(sqlList.get(0).getValue(), is("insert"));
        assertThat(sqlList.get(1).getKey(), is("get"));
        assertThat(sqlList.get(1).getValue(), is("select"));
        assertThat(sqlList.get(2).getKey(), is("delete"));
        assertThat(sqlList.get(2).getValue(), is("delete"));

    }
}
