package test;

import static org.junit.Assert.fail;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import sqlservice.SqlNotFoundException;
import sqlservice.SqlUpdateFailureException;
import sqlservice.updatable.EmbeddedDbSqlRegistry;
import sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{

    EmbeddedDatabase db;

    @Override
    protected UpdatableSqlRegistry createUPUpdatableSqlRegistry() {
        db = new EmbeddedDatabaseBuilder()
            .setType(H2)
            .addScript("classpath:/sqlRegistrySchema.sql")
            .build();
        EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
        embeddedDbSqlRegistry.setDataSource(db);
        return embeddedDbSqlRegistry;
    }

    
    @Test//6번쨰 테스트
    public void transactionalUpdate(){
        checkFindResult("SQL1", "SQL2", "SQL3"); //초기상태 확인
        //롤백후의 상태는 초기 상태와 동일 하다는 것을 보여준다.

        Map<String, String > sqlmap = new HashMap<>();
        sqlmap.put("KEY1", "Modified1");
        sqlmap.put("KEY9999!@#$", "Modified9999"); //존재하지 않는 키를 지정한다(테스트 실패, 롤백되어야한다)

        try {
            sqlRegistry.updateSql(sqlmap);
            fail();
        }catch (SqlUpdateFailureException e){
        }
        checkFindResult("SQL1", "SQL2", "SQL3");
    }
    
    @After
    public void tearDown(){
        db.shutdown();
    }
}
