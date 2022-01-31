package target;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PointcutExpressionTest {

    @Test
    public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int " +
            "target.Target.minus(int, int) throws java.lang.RuntimeException)");

        assertThat(pointcut.getClassFilter().matches(Target.class) &&
            pointcut.getMethodMatcher().matches(        //클래스 필터와 메소드 필터를 가지고와 각각비교
                Target.class.getMethod("minus", int.class, int.class), null), is(true));

        assertThat(pointcut.getClassFilter().matches(Target.class) &&
            pointcut.getMethodMatcher().matches(
                Target.class.getMethod("plus", int.class, int.class), null), is(false));

        assertThat(pointcut.getClassFilter().matches(Bean.class) &&
            pointcut.getMethodMatcher().matches(
                Target.class.getMethod("method"), null), is(false));

    }

    @Test
    public void pointcut() throws Exception {
        targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
    }


    public void pointcutMatches(String expression, Boolean expected, Class<?> clazz,
        String methodName, Class<?>... args) throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression); //모든 메소드 선택

        assertThat(pointcut.getClassFilter().matches(clazz)
            && pointcut.getMethodMatcher().matches(
            clazz.getMethod(methodName, args), null), is(expected));
    }

    public void targetClassPointcutMatches(String expression, boolean... expected)
        throws NoSuchMethodException {
        pointcutMatches(expression, expected[0], Target.class, "hello");
        pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
        pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
        pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
        pointcutMatches(expression, expected[4], Target.class, "method");
        pointcutMatches(expression, expected[5], Bean.class, "method");
    }


}
