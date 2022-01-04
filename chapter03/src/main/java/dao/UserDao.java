package dao;

import connectionMaker.ConnectionMaker;
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

	private JdbcContext jdbcContext;
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

		this.jdbcContext = new JdbcContext();
		this.jdbcContext.setDataSource(dataSource); // UserDao가 컨테이너 역할을 한다.
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
	}
}
