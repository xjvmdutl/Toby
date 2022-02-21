package learningtest.spring.ioc.bean;

import learningtest.spring.ioc.bean.Printer;

public class ConsolePrinter implements Printer {

    @Override
    public void print(String message) {
        System.out.println(message);
    }
}
