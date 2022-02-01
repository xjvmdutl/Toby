package dao;

import entity.Level;
import entity.User;
import exception.DuplicateUserIdException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import sqlservice.SqlService;


public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;

    //SQL이 분리되지 못했기 때문에 테이블이나 쿼리가 변경되게 되면 해당 파일을 다시 빌드 배포 해야된다.
    /*
    private String sqlAdd; //AddSQL을 불리할 것이다

    public void setSqlAdd(String sqlAdd) {
        this.sqlAdd = sqlAdd;
    }
    */
    /*
    private Map<String, String> sqlMap;

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }
    */

    private SqlService sqlService; //모든 DAO에서 서비스 빈을 사용하게 할 것이므로 키이름이 DAO별로 중복되지 않게 해야한다

    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    };


    public UserDaoJdbc() {
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) throws DuplicateUserIdException {

        this.jdbcTemplate.update(
            //this.sqlAdd //외부에서 주입받은 SQL을 사용하게 한다, XML 설정만으로도 SQL을 변경 가능하도록 수정하엿다
            //SQL 이 많아 질 수록 DI 해야되는 설정이 늘어나게 되는 문제가 있다
            //Map을 이용하면 키값을 이용해 SQL문을 가지고 올 수 있기 때문에 이를 활용하여 수정하여 보자
            //Map을 이용하여 관리하면 편리하긴 하지만 직접 실행 하기 전에는 문제가 발생한 지를 알기 힘들다
            //스프링 설정 파일에 SQL을 두고 관리하게 되면 1.SQL - DI 설정 정보가 뒤섞여 관리하기 힘들다 2. 꼭 빈설정 방법을 통해 XML에 담아둘 필요가 없다 3.스프링 설정파일로 부터 생성된 오브젝트와 정보는 어플리케이션을 다시 동작시키기 전에는 변경이 매우 어렵다
            //this.sqlMap.get("add")// 메소드 이름으로 SQL을 키값을 저장하고 가지고 온다
            this.sqlService.getSql("userAdd")
            , user.getId(), user.getName(), user.getPassword()
            , user.getLevel().intValue(), user.getLogin(), user.getRecommend()
            , user.getEmail()
        );


    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(
            // this.sqlMap.get("get"),
            this.sqlService.getSql("userGet"),
            new Object[]{id},
            this.userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update(
            //this.sqlMap.get("deleteAll")
            this.sqlService.getSql("userDeleteAll")
        );
    }

    public int getCount() {
        return this.jdbcTemplate.queryForInt(
            //this.sqlMap.get("getCount")
            this.sqlService.getSql("userGetCount")
        );
    }

    public void update(User user) {
        this.jdbcTemplate.update(
            //this.sqlMap.get("update"),
            this.sqlService.getSql("userUpdate"),
            user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(),
            user.getRecommend(), user.getEmail(), user.getId()
        );
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(
            //this.sqlMap.get("getAll"),
            this.sqlService.getSql("userGetAll"),
            this.userMapper);
    }
}
