package test;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import sqlservice.SqlNotFoundException;
import sqlservice.SqlUpdateFailureException;
import sqlservice.UpdatableSqlRegistry;

public abstract class AbstractUpdatableSqlRegistryTest {
    UpdatableSqlRegistry sqlRegistry;

    abstract protected UpdatableSqlRegistry createUPUpdatableSqlRegistry();// 테스트 픽스쳐를 생성하는 부분만 추상메소드로 만들어 두고 서브클래스에서 이를 구현하도록 하자

    @Before
    public void setUp(){
        sqlRegistry = createUPUpdatableSqlRegistry();
        sqlRegistry.registerSql("KEY1", "SQL1"); //각 테스트 메소드에서 사용할 초기 SQL 정보를 미리 등록한다
        sqlRegistry.registerSql("KEY2", "SQL2");
        sqlRegistry.registerSql("KEY3", "SQL3");
    }



    @Test
    public void find(){
        checkFindResult("SQL1", "SQL2", "SQL3");
    }

    protected void checkFindResult(String expect1, String expect2, String expect3) {
        assertThat(sqlRegistry.findSql("KEY1"), is(expect1));
        assertThat(sqlRegistry.findSql("KEY2"), is(expect2));
        assertThat(sqlRegistry.findSql("KEY3"), is(expect3));
    }

    @Test(expected = SqlNotFoundException.class)
    public void unknownKey(){
        //주어지 SQL에 해당하는 KEY 가 없을때 발생하는 오류
        sqlRegistry.findSql("SQL9999!@#$");
    }

    @Test
    public void updateSingle(){
        sqlRegistry.updateSql("KEY2", "Modified2");
        checkFindResult("SQL1", "Modified2", "SQL3");
    }

    @Test
    public void updateMulti(){
        Map<String, String> sqlmap = new HashMap<>();
        sqlmap.put("KEY1", "Modified1");
        sqlmap.put("KEY3", "Modified3");

        sqlRegistry.updateSql(sqlmap);
        checkFindResult("Modified1", "SQL2", "Modified3");
    }

    @Test(expected = SqlUpdateFailureException.class)
    public void updateWithNotExistingKey(){
        //주어지 SQL에 해당하는 KEY 가 없을때 발생하는 오류
        sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
    }
}
