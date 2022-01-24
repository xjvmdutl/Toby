package helllo;

import java.util.Locale;

public class HelloUpperCase implements Hello{
    Hello hello; //위임할 타깃 오브젝트(여기서는 타깃클래스의 오브젝트인 것은 알지만 다른 프록시를 추가할 수 있으므로 인터페이스로 접근한다)

    public HelloUpperCase(Hello hello) {
        this.hello = hello;
    }

    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();//위임과 부가기능 제공
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase();
    }
}
