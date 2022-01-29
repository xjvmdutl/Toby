import hello.UpperCaseHandler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DynamicProxyTest {

    /*
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
     */
    /**
     * 다이나믹 프록시 : 프록시 팩토리에 의해 런타임 시 다이나믹하게 만들어지는 오브젝트
     *  장점 : 인터페이스의 메소드가 아무리 늘어나도 추가된 메소드가 자동으로 포함된다(메소드를 일일히 추가할 필요가 없다
     *  만약 return 타입이 String이 아닐경우 런타임시에 케스팅 오류가 발생한다.
     *  따라서 타깃 오브젝트의 메소드 호출 후 리턴 타입을 확인해서 스트링인 경우만 대문자로 바꿔주고 ,나머지는 그대로 리턴하도록 해야한다.
     *
     */
    /*
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
    */
    @Test
    public void proxyFactoryBean(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget()); //타깃 설정
        pfBean.addAdvice(new UppercaseAdvice()); //부가기능을 담은 Advice 추가

        Hello proxiedHello = (Hello) pfBean.getObject(); // FactoryBean이므로 getObject()로 생성된 프록시를 가지고 온다.
        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    @Test
    public void pointcutAdvisor(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();//메소드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷 생성
        pointcut.setMappedName("sayH*"); //sayH로 시작하는 모든 메소드를 선택한다.
        /**
         * 여러개의 포인트컷 - advice 가 등록될 수 있기떄문에 이를 조합하여 advisor 타입으로 전달해야된다
         * 포인트컷-advice 조합을 advisor 라고 한다
         */
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));//포인트컷과 어드바이스를 advisor로 묶어 한번에 준다
        
        Hello proxiedHello = (Hello) pfBean.getObject(); // FactoryBean이므로 getObject()로 생성된 프록시를 가지고 온다.
        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));//포인트컷 선정조건과 맞지 않기 때문에 부가기능 제공 X
    }

    @Test
    public void classNamePointcutAdvisor(){
        //포인트컷 준비
        /**
         * 학습 테스트
         * 포인트컷에 클레스 선택기능 확장
         *
         */
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){
          public ClassFilter getClassFilter(){
              return new ClassFilter() {
                  @Override
                  public boolean matches(Class<?> clazz) {
                      return clazz.getSimpleName().startsWith("HelloT");//클래스 명이 HelloT로 시작하는 것만 선정
                  }
              };
          }
        };
        classMethodPointcut.setMappedName("sayH*");

        checkAdviced(new HelloTarget(), classMethodPointcut, true); //적용 클래스

        class HelloWorld extends HelloTarget{};
        checkAdviced(new HelloWorld(), classMethodPointcut, false); //적용클래스 아니다

        class HelloToby extends HelloTarget{};
        checkAdviced(new HelloToby(), classMethodPointcut, true);
    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfbean = new ProxyFactoryBean();
        pfbean.setTarget(target);
        pfbean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello) pfbean.getObject();

        if(adviced){
            assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
            assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        }else{
            assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
            assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        }
    }


    static class UppercaseAdvice implements MethodInterceptor {

        /**
         *  MethodInterceptor를 구현하여 해결할 수 있다.
         * 부가기능을 제공하는 오브젝트를 advice, 메소드 선정 알고리즘을 담은 오브젝트를 포인트 컷이라고 부른다.
         * 프록시는 클라이언트로 부터 요청을 받으면 먼저 포인트컷에게 부가기능을 부여할 메소드인지 확인해달라 요청한뒤, 확인을 받으면 MethodInterceptor 타입을 어드바이스를 호출한다
         * 
         */

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed(); //리플렉션 Method와 달리 메소드 실행시 타깃 오브젝트를 전달할 필요가 없다.
            //MethodInvocation은 메소드 정보와 함께 타깃 오브젝트를 알고 있기 때문이다
            return ret.toUpperCase(); //부가기능 적용
            
        }
    }



    static interface Hello {
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
    }

    static class HelloTarget implements Hello {
        @Override
        public String sayHello(String name) {
            return "Hello " + name;
        }

        @Override
        public String sayHi(String name) {
            return "Hi " + name;
        }

        @Override
        public String sayThankYou(String name) {
            return "Thank You " + name;
        }
    }

}
