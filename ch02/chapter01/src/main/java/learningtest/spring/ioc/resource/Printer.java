package learningtest.spring.ioc.resource;

import org.springframework.stereotype.Component;

@Component
public interface Printer {

    void print(String message);
}
