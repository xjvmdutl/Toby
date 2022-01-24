package handler;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransactionHandler implements InvocationHandler {
    //요청 target을 DI로 제공받는다
    private Object target; //부가기능을 제공할 타깃 오브젝트, 어떤 오브젝트이든 상관 X

    private PlatformTransactionManager transactionManager;//트렌젝션 기능을 제공하는데 필요한 트랜잭션 매니져
    private String pattern; //트랜잭션을 적용할 메소드 이름 패턴

    public void setTarget(Object target){
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 다이나믹 프록시
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().startsWith(pattern)){
            return invokeInTransaction(method, args);
        }else{
            return method.invoke(target, args);
        }
    }

    private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{ //부가 기능 수행
            Object ret = method.invoke(target, args);
            this.transactionManager.commit(status);
            return ret;
        }catch (InvocationTargetException e){ //invoke()를 이용해 메소드를 호출할때 발생하는 예외는 InvocationTargetException으로 한번 포장되서 전달된다.
            this.transactionManager.rollback(status);
            throw e.getTargetException(); //따라서 위의 예외로 받은후, getTargetException 메소드로 중첩되어 있는 예외를 가지고 와야한다.
        }
    }
}
