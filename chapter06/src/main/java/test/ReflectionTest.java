package test;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReflectionTest { //리플랙션 학습 테스트
    /**
     * 리플렉션
     * 코드 자체를 추상화 하여 접근하도록 만든것
     * String 클래스의 length() 메소드 같은경우 String.class.getMethod("length"); 이렇게 쓰면 메소드 정보를 가지고 올 수 있다.
     * Method 인터페이스에 정의된 invoke() 메소드를 사용하면 메소드를 실행시킬수 있다.
     * 메소드.invoke(name); 으로 메소드 실행
     */

    @Test
    public void invokeMethod() throws Exception{
        String name = "Spring";

        //length
        assertThat(name.length(), is(6));

        Method lengthMethod = String.class.getMethod("length");
        assertThat((Integer)lengthMethod.invoke(name), is(6));

        //charAt()
        assertThat(name.charAt(0), is('S'));
        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character)charAtMethod.invoke(name, 0), is('S'));
    }
}
