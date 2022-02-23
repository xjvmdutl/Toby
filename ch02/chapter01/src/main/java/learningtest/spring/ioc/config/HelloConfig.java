package learningtest.spring.ioc.config;

import learningtest.spring.ioc.bean.Hello;
import learningtest.spring.ioc.bean.Printer;
import learningtest.spring.ioc.bean.StringPrinter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//@Configuration //@Configuration 어노테이션이 붙어있지 않더라도 @Bean이 붙어 있는 메소드는 빈으로 등록될수 있다.
// 단, @Configuration 이 없다면, 해당 빈에 의존되어 있는 다른 빈을 호출할때 Singleton 빈이 호출되지 않고 일반적이 메소드가 호출된다
//new로 매번 새로운 객체가 만들어짐..
public class HelloConfig {
    private Printer printer;

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    @Bean
    private Hello hello(){ //외부에서 호출하지 못하도록 private 선언
        Hello hello = new Hello();
        hello.setName("Spring");
        //hello.setPrinter(printer());
        hello.setPrinter(this.printer);
        return hello;
    }

    @Bean
    private Hello hello2(){
        Hello hello = new Hello();
        hello.setName("Spring2");
        //hello.setPrinter(printer());
        hello.setPrinter(this.printer);
        return hello;
    }

    @Bean
    private Printer printer() {
        //디폴트 메타정보 항목에 따라 이 메소드로 정의되는 빈은 싱글톤이다. 
        //스프링의 특별한 조작을 통해 컨테이너에 등록된 HelloConfig 빈의 printer() 메소드는 매번 동일한 인스턴스를 리턴하도록 만들어진다
        return new StringPrinter();
    }
}
