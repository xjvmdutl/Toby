package learningtest.spring.ioc.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Resource;
import javax.inject.Provider;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Component;

public class ScopeTest {
    @Test
    public void SingletonScope(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(
            SingletonBean.class, SingletonClientBean.class
        );
        Set<SingletonBean> beans = new HashSet<>();

        beans.add(ac.getBean(SingletonBean.class)); //DL에서 싱글톤 확인
        beans.add(ac.getBean(SingletonBean.class));
        assertThat(beans.size(), is(1));

        beans.add(ac.getBean(SingletonClientBean.class).bean1);//DI 싱글톤 확인
        beans.add(ac.getBean(SingletonClientBean.class).bean2);
        assertThat(beans.size(), is(1));
    }

    static class SingletonBean{
        // 싱글톤 스코프 빈, Scope 빈 메타정보의 디폴트 값은 "singleton" 이기 때문에 별도의 스코프 설정 필요 X
    }

    static class SingletonClientBean{
        @Autowired SingletonBean bean1;
        @Autowired SingletonBean bean2;
    }

    @Test
    public void prototypeScope(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(
            PrototypeBean.class, PrototypeClientBean.class
        );
        Set<PrototypeBean> beans = new HashSet<>();

        beans.add(ac.getBean(PrototypeBean.class)); //DL방식으로 컨터이너에 빈을 요청할 때마다 새로운 빈 오브젝트가 만들어 지는 것을 확인
        assertThat(beans.size(), is(1));
        beans.add(ac.getBean(PrototypeBean.class));
        assertThat(beans.size(), is(2));

        beans.add(ac.getBean(PrototypeClientBean.class).bean1);//프로토타입 빈을 DI 할 때도 주입받는 프로퍼티 마다 다른 오브젝트가 만들어 지는 것을 확인
        assertThat(beans.size(), is(3));
        beans.add(ac.getBean(PrototypeClientBean.class).bean2);
        assertThat(beans.size(), is(4));

    }

    @Test
    public void objectFactory() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ObjectFactoryConfig.class);
        ObjectFactory<PrototypeBean> factoryBeanFactory = ac.getBean("prototypeBeanFactory", ObjectFactory.class);

        Set<PrototypeBean> bean = new HashSet<PrototypeBean>();
        for(int i=1; i<=4; i++) {
            bean.add(factoryBeanFactory.getObject());
            assertThat(bean.size(), is(i));
        }
    }

    @Configuration
    static class ObjectFactoryConfig {
        @Bean
        public ObjectFactoryCreatingFactoryBean prototypeBeanFactory() {
            ObjectFactoryCreatingFactoryBean factoryBean = new ObjectFactoryCreatingFactoryBean();
            factoryBean.setTargetBeanName("prototypeBean");
            return factoryBean;
        }
    }

    @Test
    public void serviceLocatorFactoryBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ServiceLocatorConfig.class);
        PrototypeBeanFactory factory = ac.getBean(PrototypeBeanFactory.class);

        Set<PrototypeBean> bean = new HashSet<PrototypeBean>();
        for(int i=1; i<=4; i++) {
            bean.add(factory.getPrototypeBean());
            assertThat(bean.size(), is(i));
        }
    }
    interface PrototypeBeanFactory { PrototypeBean getPrototypeBean(); }
    @Configuration
    static class ServiceLocatorConfig {
        @Bean public ServiceLocatorFactoryBean prototypeBeanFactory() {
            ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
            factoryBean.setServiceLocatorInterface(PrototypeBeanFactory.class);
            return factoryBean;
        }
    }

    @Test
    public void providerTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ProviderClient.class);
        ProviderClient client = ac.getBean(ProviderClient.class);

        Set<PrototypeBean> bean = new HashSet<PrototypeBean>();
        for(int i=1; i<=4; i++) {
            bean.add(client.prototypeBeanProvider.get());
            assertThat(bean.size(), is(i));
        }
    }
    static class ProviderClient {
        @Resource
        Provider<PrototypeBean> prototypeBeanProvider;
    }

    @Scope("prototype") //프로토타입 빈으로 생성
    @Component("prototypeBean")
    static class PrototypeBean{

    }

    static class PrototypeClientBean{
        @Autowired PrototypeBean bean1;
        @Autowired PrototypeBean bean2;
    }
}
