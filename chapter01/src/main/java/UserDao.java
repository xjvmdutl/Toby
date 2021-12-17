

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class UserDao {
	/*
	private SimpleConnectionMaker simpleConnectionMaker; //초기 한번만 오브젝트를 만들어 저장

	public UserDao() {
		this.simpleConnectionMaker = new SimpleConnectionMaker();
	}
	*/
	private ConnectionMaker connectionMaker;

	public UserDao(ConnectionMaker connectionMaker){		//UserDao를 생성하는 쪽에다가 ConnectionMaker 타입을 책임을 맡겼다
		//connectionMaker = new DConnectionMaker(); // 생성자를 호출해서 오브젝트를 생성하는 코드가 남아있다.
		//초기 한번은 어떤 클래스의 오브젝트를 사용할지를 결정하는 코드는 제거되지 않았다.
		//UserDao가 어떤 ConnectionMaker 구현 클래스의 오브젝트를 이용하게 할지를 결정하는 코드가 남았다.
		this.connectionMaker = connectionMaker;

	}

	public void add(User user) throws SQLException, ClassNotFoundException {
		//Connection c = getConnection(); //메소드 분리 패턴
		//Connection c = simpleConnectionMaker.makeNewConnection(); //UserDao 가 DB 커넥션을 가져오는 정보를 너무 많이 알고있다
		Connection c = connectionMaker.makeNewConnection();
		PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());

		ps.executeUpdate();
		ps.close();
		c.close();
	}
	
	public User get(String id) throws SQLException, ClassNotFoundException {
		Connection c = connectionMaker.makeNewConnection();
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		return user;
	}
	

	 /*
	 private Connection getConnection() throws ClassNotFoundException,SQLException {
		 Class.forName("org.h2.Driver"); 
		 Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/toby","sa",""); 
	  	 return c; 
  	 }
	 */
	//public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
}
