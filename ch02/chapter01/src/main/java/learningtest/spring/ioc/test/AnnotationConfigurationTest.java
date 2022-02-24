package learningtest.spring.ioc.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import learningtest.spring.ioc.resource.Hello;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class AnnotationConfigurationTest {
    private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/../";
    private String beanBasePath = basePath + "annotation/";

    @Test
    public void atResource() {
        ApplicationContext ac = new GenericXmlApplicationContext(basePath + "Resource.xml");

        Hello hello = ac.getBean("hello", Hello.class);

        hello.print();

        assertThat(ac.getBean("myprinter").toString(), is("Hello Spring"));
    }
}
