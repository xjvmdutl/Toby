package target;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PointcutExpressionTest {
    @Test
    public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException{
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int " + //execution = 메소드 실행에 대한 포인트컷
                "target.Target.minus(int, int) throws java.lang.RuntimeException)");
        //생략 포인트컷
        //pointcut.setExpression("execution(int minus(int, int))"); //생략된 필드는 모두 허용한다.
        //pointcut.setExpression("execution(* minus(..))"); //*,.. 와일드 카드 문법을 사용하여 갯수와 타입을 무시할수 있다
        // pointcut.setExpression("execution(* *(..))"); //모든 메소드 선택


        //Target.minus()
        assertThat(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(        //클래스 필터와 메소드 필터를 가지고와 각각비교
                        Target.class.getMethod("minus", int.class, int.class), null), is(true));
        //Target.plus()
        assertThat(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(
                        Target.class.getMethod("plus", int.class, int.class), null), is(false));

        //Bean.method()
        assertThat(pointcut.getClassFilter().matches(Bean.class) &&
                pointcut.getMethodMatcher().matches(
                        Target.class.getMethod("method"), null), is(false));

    }

    @Test
    public void pointcut() throws Exception{
        targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
    }


    public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression); //모든 메소드 선택


        assertThat(pointcut.getClassFilter().matches(clazz)
                && pointcut.getMethodMatcher().matches(
                        clazz.getMethod(methodName, args), null), is(expected));
    }

    public void targetClassPointcutMatches(String expression, boolean... expected) throws NoSuchMethodException {
        pointcutMatches(expression, expected[0], Target.class, "hello");
        pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
        pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
        pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
        pointcutMatches(expression, expected[4], Target.class, "method");
        pointcutMatches(expression, expected[5], Bean.class, "method");
    }


}
