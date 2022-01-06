package dao;

import connectionMaker.ConnectionMaker;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import strategy.JdbcContext;
import strategy.StatementStrategy;
import entity.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDao {
	private ConnectionMaker connectionMaker;
	private DataSource dataSource;

	private Connection c;

	//private JdbcContext jdbcContext;

	private JdbcTemplate jdbcTemplate;

	/*
	public void setJdbcContext(JdbcContext jdbcContext) {
		this.jdbcContext = jdbcContext;
	}
	*/
	public UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}

	public UserDao() {
	}

	public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}

	public void setDataSource(DataSource dataSource) {
		/*
		this.jdbcContext = new JdbcContext();
		this.jdbcContext.setDataSource(dataSource); // UserDao가 컨테이너 역할을 한다.
		*/
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	public void add(final User user) throws SQLException { //User는 변경되지 않음으로 final
		//해당 이름조차 없어도 되는 익명내부 클래스로 변경하자
		/*
		class AddStatement implements StatementStrategy{
			//클래스를 내부 클래스로 로컬클래스 이다.
			//User user; //로컬변수는 내부 변수에 직접 접근이 가능하다

			//public AddStatement(User user) {
			//	this.user = user;
			//}

			//단 내부 클래스에서 외부의 변수를 사용할 때는 외부 변수는 반드시 final로 표기해야한다.
			@Override
			public PreparedStatement makePrepareStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				return ps;
			}
		}
		 */
		//jdbcContextWithStatementStrategy(
		/*
		this.jdbcContext.workWithStatementStrategy(
				new StatementStrategy() {
					@Override
					public PreparedStatement makePrepareStatement(Connection c) throws SQLException {
						PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
						ps.setString(1, user.getId());
						ps.setString(2, user.getName());
						ps.setString(3, user.getPassword());
						return ps;
					}
		});
		 */
		//중간에 transaction-tx dependency 추가
		this.jdbcTemplate.update("insert into users(id,name,password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
	}

	public User get(String id) throws SQLException {
		c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);

		ResultSet rs = ps.executeQuery();
		User user = null;
		if (rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}
		rs.close();
		ps.close();
		c.close();

		if (user == null)
			throw new IllegalArgumentException();
		return user;
	}

	/**
	 * User 모든 레코드 삭제
	 *
	 * @throws SQLException
	 */
	/*
	public void deleteAll() throws SQLException {
		//해당 메소드가 클라이언트 코드가 되서 동작되어야 한다.
		//StatementStrategy st = new DeleteAllStatement();
		this.jdbcContext.workWithStatementStrategy(
				new StatementStrategy() {
			@Override
			public PreparedStatement makePrepareStatement(Connection c) throws SQLException {
				return c.prepareStatement("delete from users");
			}
		});
	}
	 */
	public void deleteAll() throws SQLException {
		//this.jdbcContext.execute("delete from users");//변경되는 SQL 구문만 파라미터로 받는다.
		//JdbcContext안에 콜백,클라이언트,템플릿이 모두 공존한다.
		/*
		this.jdbcTemplate.update(
			new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					return connection.prepareStatement("delete from users");
				}
			}
		);
		 */
		this.jdbcTemplate.update("delete from users"); //내장 콜백 사용
	}
	/**
	//JDBCContext 안으로
	private void executeSql(final String query) throws SQLException {
		this.jdbcContext.workWithStatementStrategy(
				new StatementStrategy() {
					@Override
					public PreparedStatement makePrepareStatement(Connection c) throws SQLException {
						return c.prepareStatement(query);
					}
				});
	}
	*/
	/*
	private PreparedStatement makeStatement(Connection c) throws SQLException {
		//자주 변경되는 부분을 메소드로 바꿨지만 오히려 반대로 되었다.
		return c.prepareStatement("delete from users");
	}
	*/
	//abstract protected PreparedStatement makeStatement(Connection c) throws SQLException;

	/**
	 * User 테이블 레코드 갯수 반환
	 * @return int
	 * @throws SQLException
	 */
	public int getCount() throws SQLException{
		/*
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("select count(*) from users");
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		}catch (SQLException e){
			throw e;
		}finally {
			if(rs != null){
				try{
					rs.close();
				}catch (SQLException e){
				}
			}
			if(ps != null){
				try{
					ps.close();
				}catch (SQLException e){
				}
			}
			if(c != null){
				try{
					c.close();
				}catch (SQLException e){
				}
			}
		}
		 */
		/*
		return this.jdbcTemplate.query(
				//콜백 2개 필요
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						return connection.prepareStatement("select count(*) from users");
					}
				}, new ResultSetExtractor<Integer>() {  //resultSet을 받아 추출한 값을 리턴한다 //ResultSet으로 부터 값 추출
					@Override
					public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
						resultSet.next();
						return resultSet.getInt(1);
					}
				}
		);
		 */
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}
}
