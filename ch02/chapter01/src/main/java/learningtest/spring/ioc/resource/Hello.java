package learningtest.spring.ioc.resource;

import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class Hello {

    private String name;

    @Resource
    private Printer printer;

    public Hello() {
    }

    public Hello(String name, Printer printer) {
        this.name = name;
        this.printer = printer;
    }

    public String sayHello() {
        return "Hello " + name;
    }

    public void print() {
        this.printer.print(this.sayHello());
    }
}
