package dao;

import entity.Level;
import entity.User;
import exception.DuplicateUserIdException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;

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
            "insert into users(id, name, password, level, login, recommend, email) values(?, ?, ?, ?, ?, ?, ?)"
            , user.getId(), user.getName(), user.getPassword()
            , user.getLevel().intValue(), user.getLogin(), user.getRecommend()
            , user.getEmail()
        );


    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
            new Object[]{id},
            this.userMapper);
    }

    /**
     * User 모든 레코드 삭제
     *
     * @throws SQLException
     */
    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    /**
     * User 테이블 레코드 갯수 반환
     *
     * @return int
     * @throws SQLException
     */
    public int getCount() {
        return this.jdbcTemplate.queryForInt("select count(*) from users");
    }

    public void update(User user) {
        this.jdbcTemplate.update(
            "update users set name = ?, password = ?, level = ?, login = ?, recommend = ?, email = ? where id = ?",
            user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(),
            user.getRecommend(), user.getEmail(), user.getId()
        );
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id",
            this.userMapper);
    }
}
