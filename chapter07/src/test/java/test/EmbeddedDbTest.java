package test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class EmbeddedDbTest {
    EmbeddedDatabase db;
    SimpleJdbcTemplate template; //JDBCTemplate를 더 편리하게 사용할 수 있게 확장한 템플릿

    @Before
    public void setUp(){
        db = new EmbeddedDatabaseBuilder()
            .setType(H2)
            .addScript("classpath:/schema.sql")
            .addScript("classpath:/data.sql")
            .build();
        template = new SimpleJdbcTemplate(db);
    }

    @After
    public void tearDown(){
        //매 테스트를 진행한 뒤 DB를 종료한다
        db.shutdown();
    }

    @Test
    public void initData(){
        //초기화 스크립트를 통해 등록된 데이터를 검증하는 테스트
        assertThat(template.queryForInt("select count(*) from sqlmap"), is(2));

        List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
        assertThat((String) list.get(0).get("key_"), is("KEY1"));
        assertThat((String) list.get(0).get("sql_"), is("SQL1"));
        assertThat((String) list.get(1).get("key_"), is("KEY2"));
        assertThat((String) list.get(1).get("sql_"), is("SQL2"));
    }

    @Test
    public void insert(){
        template.update(
            "insert into sqlmap(key_, sql_) values(?, ?)", "KEY3", "SQL3"
        );
        assertThat(template.queryForInt("select count(*) from sqlmap"), is(3));

    }
}
