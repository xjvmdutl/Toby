package dao;

import entity.User;
import exception.DuplicateUserIdException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class UserDaoJdbc implements UserDao {
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


	public UserDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);

		/*
		 trt{
		 	//JDBC를 이용해 User 정보를 DB에 추가하는 코드
		 	//그런 기능이 있는 다른 SQLException을 던지는 메소드를 호출하는 코드
		 }catch(SQLException e){
		 	//이제 더이상 이 add()메소드를 사용하는 오브젝트는 SQLException을 처리하기 위해 불필요한 throws를 선언할 필요 x
		 	//필요한 경우, 아이디 중복상황을 처리하기 위해 DuplicatedUserIdException 을 이용할 수 있다.
		 	if(e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY){
		 		throw new DuplicateUserIdException(e); //내가 만든 클래스
		 		//예외 전환
		 	}else{
		 		throw new RuntimeException(e); //예외 포장
		 	}
		 }
		 */
	}

	public void add(final User user) throws DuplicateUserIdException {
		/*
		try {
			this.jdbcTemplate.update("insert into users(id,name,password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());

		}catch (DuplicateKeyException e){
			throw new DuplicateUserIdException(e);//예외를 전환할 때는 원인이 되는 예외를 중첩하는것이 좋다.
		}
		 */
		this.jdbcTemplate.update("insert into users(id,name,password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());


	}

	public User get(String id)  {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?",
				new Object[]{id},
				this.userMapper);
	}

	/**
	 * User 모든 레코드 삭제
	 *
	 * @throws SQLException
	 */
	public void deleteAll()  {
		this.jdbcTemplate.update("delete from users");
	}

	/**
	 * User 테이블 레코드 갯수 반환
	 * @return int
	 * @throws SQLException
	 */
	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id",
				this.userMapper);
	}
}
