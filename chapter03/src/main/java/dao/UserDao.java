package dao;

import connectionMaker.ConnectionMaker;
import strategy.DeleteAllStatement;
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

	public UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}

	public UserDao() {
	}

	public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void add(User user) throws SQLException {
		c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());

		ps.executeUpdate();
		ps.close();
		c.close();
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
		StatementStrategy st = new DeleteAllStatement();
		jdbcContextWithStatementStrategy(st);
	}

	private void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		/**
		 * try/catch/finally 문법 사용
		 */
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = dataSource.getConnection();

			//ps = makeStatement(c);//해당 위치에서 에러 발생시 메소드 실행이 중지, //변하는 부분
			//1.변하는 부분만 메소드로 빼기
			//2.템플릿메소드 패턴의 적용 : 변하지 않는 부분은 슈퍼클래스에 두고 변하는 부분은 추상메소드로 정의해서 서브클래스에서 오버라이드 하여 새롭게 정의해 쓰도록 한다
			//메소드를 만들때마다 서브 클래스를 만들어서 사용해야 한다
			//3.전략패턴의 적용 : 오브젝트를 둘로 분리하고 클래스 레벨에서는 인터페이스를 통해서만 의존하도록 만드는 전략패턴
			//StatementStrategy strategy = new DeleteAllStatement(); //Strategy 가 인터페이스 뿐만 아니라 특정 구현 클래스 DeleteAllStatement까지 알고 있는게 이상하다
			//ps = strategy.makeStatement(c);
			ps = stmt.makePrepareStatement(c);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally { //무조건 반환이 되도록
			if (ps != null) {//해당 리소스들이 제대로 반환되지 않을 수 있다.
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
				}
			}
		}
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
