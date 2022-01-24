package test;

import helllo.Hello;
import helllo.HelloTarget;
import helllo.HelloUpperCase;
import helllo.UpperCaseHandler;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SimpleProxyTest {


    @Test
    public void simpleProxy(){
        Hello hello = new HelloTarget();
        assertThat(hello.sayHello("Toby"), is("Hello Toby"));
        assertThat(hello.sayHi("Toby"), is("Hi Toby"));
        assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));
    }

    @Test
    public void helloUpperCase(){
        Hello proxyHello = new HelloUpperCase(new HelloTarget()); //프록시를 통해 접근하도록 한다.
        assertThat(proxyHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxyHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxyHello.sayThankYou("Toby"), is("THANK YOU TOBY"));

    }
    /**
     * 다이나믹 프록시 : 프록시 팩토리에 의해 런타임 시 다이나믹하게 만들어지는 오브젝트
     *  장점 : 인터페이스의 메소드가 아무리 늘어나도 추가된 메소드가 자동으로 포함된다(메소드를 일일히 추가할 필요가 없다
     *  만약 return 타입이 String이 아닐경우 런타임시에 케스팅 오류가 발생한다.
     *  따라서 타깃 오브젝트의 메소드 호출 후 리턴 타입을 확인해서 스트링인 경우만 대문자로 바꿔주고 ,나머지는 그대로 리턴하도록 해야한다.
     *
     */
    @Test
    public void DynamicProxy(){
        Hello proxiedHello = (Hello) Proxy.newProxyInstance( //생성된 다이내믹 프로시 오브젝트는 Hello인터페이스를 구현하고 있으므로 Hello타입으로 캐스팅 하여도 안전하다
                getClass().getClassLoader(), //동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용될 클래스 로더
                new Class[]{ Hello.class}, //구현할 인터페이스
                new UpperCaseHandler(new HelloTarget()) //부가기능과 위임기능을 담은 InvocationHandler
        );
        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }
}
