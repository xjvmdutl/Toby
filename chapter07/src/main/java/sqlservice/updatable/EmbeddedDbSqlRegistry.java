package sqlservice.updatable;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import sqlservice.SqlNotFoundException;
import sqlservice.SqlUpdateFailureException;
import sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {

    SimpleJdbcTemplate jdbcTemplate;

    TransactionTemplate transactionTemplate; // JDBCTemplate와 트랜잭션 동기화를 해주는 트랜잭션 탬플릿이다. 멀티스레드 환경에서 공유 가능

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        transactionTemplate = new TransactionTemplate(
            new DataSourceTransactionManager(dataSource) //DataSource로 트랜잭션메니져를 만들고 template을 생성한다
        );
    }

    @Override
    public void registerSql(String key, String sql) {
        jdbcTemplate.update("insert into sqlmap(key_, sql_) values(?,?)", key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        try {
            return jdbcTemplate
                .queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
        } catch (EmptyResultDataAccessException e) {
            throw new SqlNotFoundException(key);
        }
    }

    @Override
    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        int affected = jdbcTemplate.update("update sqlmap set sql_ = ? where key_ = ?", sql, key);
        if (affected == 0) {
            throw new SqlUpdateFailureException(key);
        }
    }

    @Override
    public void updateSql(final Map<String, String> sqlmap)
        throws SqlUpdateFailureException { //익명 내부 클래스로 만들어지는 콜백 오브젝트 안에서 사용되는 것이므로 final로 만든다
        //반드시 트랜잭션을 개념이 들어가야 하지만 복잡하게 빈으로 등록할 필요가 없다(DB에 대한 트랜잭션 매니져를 공유할 필요가 없기 떄문)
        transactionTemplate.execute(
            new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    for (Map.Entry<String, String> entry : sqlmap
                        .entrySet()) {  //트랜잭션 경계안에서 동작할 코드를 콜백형태로 만들고 execute 메소드에 전달
                        updateSql(entry.getKey(), entry.getValue());
                    }
                }
            }
        );
    }
}
