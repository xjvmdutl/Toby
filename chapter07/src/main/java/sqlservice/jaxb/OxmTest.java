package sqlservice.jaxb;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/OxmTest-context.xml") //클래스이름-context.xml 파일을 사용
public class OxmTest {

    @Autowired
    Unmarshaller unmarshaller;//스프링 테스트가 테스트용 어플리케이션 컨택스트에서 Unmarshaller 인터페이스를 테스트가 시작전 주입해 준다
    
    @Test
    public void readSqlmap() throws JAXBException {
        /*
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext
            .newInstance(contextPath);//바인딩용 클래스 위치를 가지고 JAXB 컨텍스트를 만든다.
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(
            OxmTest.class.getResourceAsStream("/sqlservice/jaxb/sqlmap.xml")
        );
        List<SqlType> sqlList = sqlmap.getSql();

        assertThat(sqlList.size(), is(3));
        assertThat(sqlList.get(0).getKey(), is("add"));
        assertThat(sqlList.get(0).getValue(), is("insert"));
        assertThat(sqlList.get(1).getKey(), is("get"));
        assertThat(sqlList.get(1).getValue(), is("select"));
        assertThat(sqlList.get(2).getKey(), is("delete"));
        assertThat(sqlList.get(2).getValue(), is("delete"));

         */
    }

    @Test
    public void unmarshallSqlMap() throws XmlMappingException, IOException, JAXBException {
        Source xmlSource = new StreamSource(
            getClass().getResourceAsStream("/sqlservice/jaxb/sqlmap.xml")
        );
        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource); //어떤 OXM기술이든 언마샬이 한줄이면 된다
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
