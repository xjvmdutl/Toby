package aop;

import handler.TransactionHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> { //범용적 사용을 위해 Object를 사용
    Object target;
    PlatformTransactionManager transactionManager;
    String pattern;
    Class<?> serviceInterface; //다이나믹 프록시를 생성할 때 필요하다.

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        //DI 받은 정보를 이용해서 TransactionHandler를 사용하는 다이나믹 프록시를 생성한다.
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(target);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern(pattern);
        return Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[]{serviceInterface}, txHandler
        );
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface; //DI받는 인터페이스 타입에 따라서 Object 타입이 달라진다.
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
