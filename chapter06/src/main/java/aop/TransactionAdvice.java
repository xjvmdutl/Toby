package aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.h2.mvstore.db.TransactionStore;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {
    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable { //타깃을 호출하는 기능을 가진 콜백 오브젝트를 프록시로부터 받는다.
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Object ret = invocation.proceed(); //콜백을 호출해서 타깃의 메소드를 실행한다.
            this.transactionManager.commit(status);
            return ret;
        }catch (RuntimeException e){ //JDK 다이내믹 프록시가 제공하는 Method와는 달리 스프링의 MethodInvocation을 톹ㅇ한 타깃 호출은 예외가 포장되지 않고 타깃에서 보낸 그대로 전달된다
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
