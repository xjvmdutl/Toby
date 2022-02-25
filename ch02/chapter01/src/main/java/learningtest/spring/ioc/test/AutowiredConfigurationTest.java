package learningtest.spring.ioc.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import learningtest.spring.ioc.autowired.Hello;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class AutowiredConfigurationTest {
    private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/../";
    @Test
    public void autowired() {
        ApplicationContext ac = new GenericXmlApplicationContext(basePath + "Autowired.xml");

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
    }

}
