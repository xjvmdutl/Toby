package dao;

import entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class UserDao {
	private JdbcTemplate jdbcTemplate;

	private RowMapper<User> userMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			return user;
		}
	};


	public UserDao() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(final User user) throws SQLException {
		this.jdbcTemplate.update("insert into users(id,name,password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
	}

	public User get(String id) throws SQLException {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?",
				new Object[]{id},
				this.userMapper);
	}

	/**
	 * User 모든 레코드 삭제
	 *
	 * @throws SQLException
	 */
	public void deleteAll() throws SQLException {
		this.jdbcTemplate.update("delete from users");
	}

	/**
	 * User 테이블 레코드 갯수 반환
	 * @return int
	 * @throws SQLException
	 */
	public int getCount() throws SQLException{
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id",
				this.userMapper);
	}
}
