package learningtest.spring.ioc.bean;

import javax.annotation.Resource;

public class Hello {

    String name;
    Printer printer;

    public Hello() {
    }

    public Hello(String name, Printer printer) {
        this.name = name;
        this.printer = printer;
    }

    public String sayHello(){ //프로퍼티로 DI 받은 이름을 이용해 간단한 인사문구 만들기
        return "Hello " + name;
    }

    public void print(){
        this.printer.print(sayHello());
        /*
            DI에 의해 의존 오브젝트로 제공받은 Printer 타입의 오브젝트에게 출력작업을 위임한다.
            구체적으로 어떤 방식으로 출력하는지는 상관하지 않는다.
            어떤 방식으로 출력하도록 변경해도 Hello 코드는 수정할 필요 X
        */
    }


    public void setName(String name) {
        //인사 문구에 쓸 이름을 String 값으로 DI 받을수 있다
        this.name = name;
    }


    public Printer getPrinter() {
        return printer;
    }

    @Resource(name = "printer")
    public void setPrinter(Printer printer) {
        //출력을 위해 사용할 Printer 인터페이스를 구현한 오브젝트를 DI 받는다
        this.printer = printer;
    }
}
