package test;

import sqlservice.updatable.ConcurrentHashMapSqlRegistry;
import sqlservice.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{
    //중복이 발생하는 Test를 제거할 수 부모클래스로 두어 이를 상속받아 사용할 수 있다.
    //@Test메소드를 모두 상속받아서 자신의 테스트로 활용한다.
    @Override
    protected UpdatableSqlRegistry createUPUpdatableSqlRegistry() {
        return new ConcurrentHashMapSqlRegistry();
    }
}
