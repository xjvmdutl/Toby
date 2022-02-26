package learningtest.spring.ioc.test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.sql.DataSource;
import learningtest.spring.ioc.autowired.Hello;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class PropertyPlaceHolderConfigureTest {
    private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/../";
    @Test
    public void propertyPlaceHolderConfigurerer() {
        ApplicationContext ac = new GenericXmlApplicationContext(basePath + "PropertyPlaceHolderConfigure.xml");
        DataSource dataSource = ac.getBean("dataSource", DataSource.class);
        //${}로 database.properties 에 key를 치환하여 <property> 의 value값에서 치환자와 일치하는 것을 찾는다.
        // 이때 ${} 치환은 PropertyPlaceHolderConfigurer 빈이 담당한다.
        //propertyPlaceHolderConfigurerer 는 빈 펙토리 후처리기이다
        //참고 : 빈펙토리 후 처리기는 빈 설정 메타정보가 모두 준비됐을 때 빈 메타정보 자체를 조작하기 위해 사용된다.
        //프로퍼티 파일에 대체할 만한 키 값을 찾지 못하면 ${db.username}이라는 치환자 이름이 username의 프로퍼티 값으로 남아 있게 된다(예외가 발생하지 않는다는 점을 주의)
        assertThat(dataSource, is(notNullValue()));
    }
    @Test
    public void spEL() {
        ApplicationContext ac = new GenericXmlApplicationContext(basePath + "SpEL.xml");
        //spEl 같은 경우도 오타시 오류가 발생하지 않기 때뮨에 주의해서 사용
        DataSource dataSource = ac.getBean("dataSource", DataSource.class);
        assertThat(dataSource, is(notNullValue()));
    }
}
