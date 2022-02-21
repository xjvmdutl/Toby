package learningtest.spring.ioc.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import learningtest.spring.ioc.bean.Hello;
import learningtest.spring.ioc.bean.Printer;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class ApplicationContextTest {
    private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/../";

    @Test
    public void ParentChildMakeTest(){
        ApplicationContext parent = new GenericXmlApplicationContext(
            basePath + "ParentContext.xml"); //GenericXmlApplicationContext 해당 클래스는 XMl 설정의 Root Context만 가능하다
        GenericApplicationContext child = new GenericApplicationContext(parent); //자식 같은 경우 상세 설정이 필요
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
        reader.loadBeanDefinitions(basePath + "ChildContext.xml");
        child.refresh();    //reader를 통해 설정을 읽은 경우에는 반드시 refresh() 를 통해 초기화 해야한다


        //부모 자식 관계가 연결이 되었기 떄문에 자식 컨택스트에서 빈이 없다면 부모에서 탐색한다(printer)
        Printer printer = child.getBean("printer", Printer.class);
        assertThat(printer, is(notNullValue()));

        //부모 , 자식 컨텍스트 모두 빈이 존재할 경우 자식 컨택스트가 우선순위가 있다(hello의 property)
        //아무리 스프링이 부모-자식 관계에서의 빈 설정 같은경우 우선순위를 준다고 하여도 중복되는 일은 피해야한다(예기치 못한 오류,버그가 발생할 수 있기 떄문)
        Hello hello = child.getBean("hello", Hello.class);
        assertThat(hello, is(notNullValue()));
        
        hello.print();
        assertThat(printer.toString(), is("Hello Child")); //getBean()으로 가져온 hello 빈은 자식 컨택스트 존재하는것을 확인할 수 있다
    }
}
