package aop;

import entity.User;
import java.util.List;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import service.UserService;

public class UserServiceTx implements UserService {

    UserService userService; //타깃 오브젝트
    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void add(User user) {
        userService.add(user);
    }

    @Override
    public User get(String id) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public void upgradeLevels() {
        TransactionStatus status = this.transactionManager
            .getTransaction(new DefaultTransactionDefinition());
        try {
            userService.upgradeLevels();
            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }

    }
}
