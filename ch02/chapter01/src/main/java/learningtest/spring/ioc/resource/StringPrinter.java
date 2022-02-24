package learningtest.spring.ioc.resource;

import org.springframework.stereotype.Component;

@Component("myprinter")
public class StringPrinter implements Printer {

    private StringBuffer buffer = new StringBuffer();

    @Override
    public void print(String message) {
        this.buffer.append(message); //내장버퍼에 메시지를 추가한다
    }

    public String toString(){
        return this.buffer.toString();
    }
}
