package learningtest.spring.ioc;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import learningtest.spring.ioc.scanner.service.MyService;
import learningtest.spring.ioc.scanner.service.ServiceMarker;
import learningtest.spring.ioc.scanner.dao.MyDao;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Repository;

public class JavaCodeBeanDefinitionTest {
    @Test
    public void componentScan() {
        // basePackages
        AnnotationConfigApplicationContext ac1 = new AnnotationConfigApplicationContext(C1.class);
        assertThat(ac1.getBean(MyDao.class), is(notNullValue()));
        assertThat(ac1.getBean(MyService.class), is(notNullValue()));

        // excludes
        AnnotationConfigApplicationContext ac2 = new AnnotationConfigApplicationContext(C2.class);
        try {
            ac2.getBean(MyDao.class);
            fail();
        }
        catch(NoSuchBeanDefinitionException e) {}
        assertThat(ac2.getBean(MyService.class), is(notNullValue()));

        // excludes
        AnnotationConfigApplicationContext ac3 = new AnnotationConfigApplicationContext(C3.class);
        try {
            ac3.getBean(MyDao.class);
            fail();
        }
        catch(NoSuchBeanDefinitionException e) {}
        assertThat(ac3.getBean(MyService.class), is(notNullValue()));

        // basePackageClasses
        AnnotationConfigApplicationContext ac4 = new AnnotationConfigApplicationContext(C4.class);
        try {
            ac4.getBean(MyDao.class);
            fail();
        }
        catch(NoSuchBeanDefinitionException e) {}
        assertThat(ac4.getBean(MyService.class), is(notNullValue()));

    }



    @Configuration
    @ComponentScan("learningtest.spring.ioc.scanner")//마커 인터페이스를 사용하면 text로 인한 오타의 오류를 방지할 수 있다
    //@ComponentScan(basePackageClasses = ServiceMarker.class) ///마커 인터페이스를 이용한 빈 스캔 패키지 지정
    static class C1  {

    }


    @Configuration
    @ComponentScan(basePackages={"learningtest.spring.ioc.scanner"},
        excludeFilters=@Filter(Repository.class)
    )
    static class C2 {}

    @Configuration
    @ComponentScan(basePackages={"learningtest.spring.ioc.scanner"},
        excludeFilters=@Filter(type= ASSIGNABLE_TYPE, value=MyDao.class)
    )
    static class C3 {}
    @Configuration
    @ComponentScan(basePackageClasses={ServiceMarker.class})
    static class C4 {}

    @Test
    public void atImport() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        ac.getBean(DataConfig.class);
        ac.getBean(AppConfig.class);
    }

    @Configuration
    @Import(DataConfig.class)
    static class AppConfig {
    }

    @Configuration
    static class DataConfig {
    }
    @Test
    public void atImportResource() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(MainConfig.class);
        assertThat((String)ac.getBean("name"), is("Toby"));
    }

    @Configuration
    @ImportResource("learningtest/spring/ioc/extra.xml")
    static class MainConfig {
    }

}
