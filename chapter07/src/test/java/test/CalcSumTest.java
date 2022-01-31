package test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import strategy.Calculator;

public class CalcSumTest {

    Calculator calculator;
    String numFilePath;

    @Before
    public void setUp() {
        this.calculator = new Calculator();
        this.numFilePath = getClass()
            .getResource("numbers.txt")
            .getPath();
    }


    @Test
    public void sumOfNumbers() throws IOException {
        assertThat(calculator.calcSum(numFilePath), is(10));
    }

    @Test
    public void multiOfNumbers() throws IOException {
        assertThat(calculator.calcMultiply(numFilePath), is(24));
    }

    @Test
    public void concatenate() throws IOException {
        assertThat(calculator.concatenate(numFilePath), is("1234"));
    }
}
