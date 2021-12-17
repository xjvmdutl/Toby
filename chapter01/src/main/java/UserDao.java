

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public abstract class UserDao {
	
	
	public void add(User user) throws SQLException, ClassNotFoundException {
		Connection c = getConnection(); //메소드 분리 패턴
		PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		ps.executeUpdate();
		ps.close();
		c.close();
	}
	
	public User get(String id) throws SQLException, ClassNotFoundException {
		Connection c = getConnection();
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
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		UserDao dao = new NUserDao();
		User user = new User();
		user.setId("whoteShip");
		user.setName("백기선");
		user.setPassword("married");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2 = dao.get(user.getId());
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		System.out.println(user2.getId() + " 조회 성공");
	}
	 /*
	 private Connection getConnection() throws ClassNotFoundException,SQLException {
		 Class.forName("org.h2.Driver"); 
		 Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/toby","sa",""); 
	  	 return c; 
  	 }
	 */
	public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
}
