package helllo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;

public class UpperCaseHandler implements InvocationHandler {
    /*
    Hello target;

    public UpperCaseHandler(Hello target){
        this.target = target;
    }
    */
    Object target;

    public UpperCaseHandler(Object target) { //어떤 인터페이스를 구현한 타깃에도 구현 가능하다.
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /*
        String ret = (String) method.invoke(target, args); //타깃으로 부터 위임, 인터페이스의 메소드 호출에 모두 적용된다
        return ret.toUpperCase(); //부가 기능 제공
         */
        /*
        Object ret = method.invoke(target, args);
        if(ret instanceof String) {
            return ((String)ret).toUpperCase();
        }else{
            return ret;
        }
         */
        Object ret = method.invoke(target, args);
        if(ret instanceof String && method.getName().startsWith("say")) {//메소드의 시작 이름으로도 할수 있다.
            return ((String)ret).toUpperCase();
        }else{
            return ret;
        }
    }
}
