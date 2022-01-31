package hello;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UpperCaseHandler implements InvocationHandler {

    Object target;

    public UpperCaseHandler(Object target) { //어떤 인터페이스를 구현한 타깃에도 구현 가능하다.
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(target, args);
        if (ret instanceof String && method.getName().startsWith("say")) {
            return ((String) ret).toUpperCase();
        } else {
            return ret;
        }
    }
}
