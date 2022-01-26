package aop;

import entity.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import service.UserService;

public class UserServiceTx implements UserService {
    /**
     * 이렇게 분리하여 얻는 장점
     * 비지니스 로직을 담당하고 있는 UserServiceImpl의 코드를 작성할 때는 트랜잭션과 같은 기술적인 내용에는 신경쓰지 않아도 된다.
     *      트랜잭션은 DI를 이용해 UserService와 같은 트랜잭션 기능을 가진 오브젝트가 먼저저 실행 되도록 만들기만 하면된다.
     */
    /**
     * 고립되는 테스트 만들기
     * 단위 테스트는 테스트의 범위가 좁으면 좁을수록 오류를 찾고 에러를 발견하기 쉽다.
     * 따라서 의존되는 관계를 최대한 끊어야 한다.
     * 테스트 스텁을 이용하여 의존관계에 아무런 동작을 하지 않게 할수 있지만 void형을 반환할 경우 해당 결과를 테스트 스텁에서 저장하고, 반환하는 역할을 해야한다.
     *
     */
    /**
     * 프록시를 도와주는 프레임워크 = java.lang.reflect
     * 프록시 기능
     * 1. 타깃과 같은 메소드를 구현하고 있다가 메소드가 호출되면 타깃 오브젝트로 위임한다.
     * 2. 지정된 요청에 대해서는 부가기능을 수행한다
     * 프록시 패턴을 만들기 힘든 이유
     * -> 타깃의 인터페이스를 구현하고 위임하는 코드를 작성하기 번거롭다.
     * -> 부가 기능 코드가 중복될 가능성이 많다.

     */
    UserService userService; //타깃 오브젝트
    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    //DI받은 UserService 오브젝트에 모든 기능을 위임한다.
    @Override
    public void add(User user) {
        userService.add(user); //메소드 구현과 위임
    }

    @Override
    public void upgradeLevels() { //메소드 구현
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); 
        try{ //부가 기능 수행
            userService.upgradeLevels(); //위임
            this.transactionManager.commit(status);
        }catch (RuntimeException e){
            this.transactionManager.rollback(status);
            throw e;
        }

    }
}
