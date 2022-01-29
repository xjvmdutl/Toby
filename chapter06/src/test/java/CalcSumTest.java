import org.junit.Before;
import org.junit.Test;
import strategy.Calculator;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CalcSumTest {
    Calculator calculator;
    String numFilePath;

    @Before
    public void setUp(){
        this.calculator = new Calculator();
        this.numFilePath = getClass()
                .getResource("numbers.txt")
                .getPath();
    }


    @Test
    public void sumOfNumbers() throws IOException{
        assertThat(calculator.calcSum(numFilePath),is(10));
    }

    @Test
    public void multiOfNumbers() throws IOException{
        assertThat(calculator.calcMultiply(numFilePath),is(24));
    }

    @Test
    public void concatenate() throws IOException{
        assertThat(calculator.concatenate(numFilePath),is("1234"));
    }
}
