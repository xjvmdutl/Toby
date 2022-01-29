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
        /**
         * 트랜잭션 속성 지정을 위한 TransactionInterceptor
         * Properties, PlatformTransactionManger 속성값을 가지고 있다.
         * Properties 같은 경우 MapType 오브젝트를 전달받고, 이는 메소드 패턴 - 트랜잭션 속성을 키-value로 가지고 있다.
         * 트랜잭션 속성
         * 1) PROPAGATION_NAME : 트랜잭션 전파 방식, 필수 항목이다.
         * 2) ISOLATION_NAME : 격리수준, 생략가능하다(생략시 디폴트 격리수준)
         * 3) readOnly : 읽기전용 항목, 생략가능하다(생략시 읽기 전용X)
         * 4) timeout_NNNN : 제한시간, 생략가능하다, 초단위 시간을 뒤에 붙힌다
         * 5) -Exception1 : 체크 예외 중에서 롤백대상으로 추가할 것을 넣는다.(Runtime예외x)
         * 6) +Exception2 : Runtime 예외지만 롤백시키지 않을 예외를 넣는다
         *
         */
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //트랜잭션 정의를 통한 4가지 조건
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
