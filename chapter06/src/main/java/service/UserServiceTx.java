package service;

import entity.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserServiceTx implements UserService{
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
    //
    //1.
    //      ->
    UserService userService;
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
        userService.add(user);
    }

    @Override
    public void upgradeLevels() {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            userService.upgradeLevels();
            this.transactionManager.commit(status);
        }catch (RuntimeException e){
            this.transactionManager.rollback(status);
            throw e;
        }

    }
}
