package learningtest.spring.ioc.autowired;

import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Hello {
    @Value("Spring")
    private String name;

    @Autowired
    private Printer printer;
    //@Autowired //해당 방식으로 같은 타임을 여러빈을 조회 가능하다
    //private Map<String, Printer> printerMap; //단 충돌을 피하기 위해 사용하는 것이 아닌 의도적으로 타입이 같은 빈을 등록하고 이를 모두 참조하거나 선별적으로 필요한 빈을 찾을 떄 사용하는 것이 좋다

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

    public void setName(String name) {
        this.name = name;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }
}
