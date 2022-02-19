package learningtest;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import hello.Hello;
import hello.StringPrinter;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;

public class HelloTest {

    @Test
    public void registerBeanWithDependency() {
        StaticApplicationContext ac = new StaticApplicationContext();

        ac.registerBeanDefinition("printer", new RootBeanDefinition(
            StringPrinter.class)); //StringPrinter 클래스 타입이며 printer 이름을 가진 빈을 등록
        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        helloDef.getPropertyValues().addPropertyValue("printer",
            new RuntimeBeanReference("printer")); //아이디가 printer 인 레퍼런스를 프로퍼티로 등록
        ac.registerBeanDefinition("hello", helloDef);

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
        //Hello 클래스의 print() 메소드는 DI 된 Printer 타입의 오브젝트에게 요청하여 인삿말을 출력한다
        //이 결과를 스트링으로 저장해두는 Printer 빈을 통해 확인한다

    }

    @Test
    public void registerBeanWithNotClass() {
        StaticApplicationContext ac = new StaticApplicationContext(); //Ioc 컨테이너 생성, 생성과 동시에 컨테이너 동작

        ac.registerSingleton("hello1", Hello.class); //Hello클래스를 hello1이라는 이름의 싱글톤 빈으로 컨터이너에 등록한다

        Hello hello1 = ac.getBean("hello1", Hello.class);
        assertThat(hello1, is(notNullValue()));

        BeanDefinition helloDef = new RootBeanDefinition(
            Hello.class); //빈 메타정보를 담은 오브젝트를 만든다. 빈클래스는 Hello지정
        helloDef.getPropertyValues().addPropertyValue("name", "Spring"); //빈의 name 프로퍼티에 들어갈 값을 지정
        ac.registerBeanDefinition("hello2", helloDef); //앞에서 생성한 빈 메타정보를 hello2라는 이름을 가진 빈으로 해서 등록한다

        //빈은 오브젝트 단위로 만들어 지고 관리되기 떄문에 같은 클래스 타입이더라도 두개를 등록하면 서로 다른 빈이 생성되는것을 알 수 있다
        Hello hello2 = ac.getBean("hello2", Hello.class);

        assertThat(hello2.sayHello(), is("Hello Spring"));
        assertThat(hello1, is(not(hello2))); //서로 같은 클래스 타입이지만 별도의 오브젝트로 생성된것을 확인할 수 있다
        assertThat(ac.getBeanFactory().getBeanDefinitionCount(),
            is(2)); //Ioc 컨테이너에 등록된 빈 설정 메타정보를 가지고 올 수 있다
    }
}
