

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class UserDao {

	//private static UserDao INSTANCE; //싱글톤
	/*
	private SimpleConnectionMaker simpleConnectionMaker; //초기 한번만 오브젝트를 만들어 저장

	public UserDao() {
		this.simpleConnectionMaker = new SimpleConnectionMaker();
	}
	*/
	private ConnectionMaker connectionMaker;// 초기에 설정하면 사용중에는 바뀌지 않는 읽기전용 인스턴스 변수
	//자신이 사용하는 다른 싱글톤 빈을 저장하려는 용도라면 인스턴스 변수를 사용해도 좋다.
	private Connection c; //매번 새로운 값으로 바뀌는 정보를 담은 인스턴스 변수, 심각한 문제 발생
	private User user;

	public UserDao(ConnectionMaker connectionMaker){		//UserDao를 생성하는 쪽에다가 ConnectionMaker 타입을 책임을 맡겼다
		//connectionMaker = new DConnectionMaker(); // 생성자를 호출해서 오브젝트를 생성하는 코드가 남아있다.
		//초기 한번은 어떤 클래스의 오브젝트를 사용할지를 결정하는 코드는 제거되지 않았다.
		//UserDao가 어떤 ConnectionMaker 구현 클래스의 오브젝트를 이용하게 할지를 결정하는 코드가 남았다.
		this.connectionMaker = connectionMaker;
	}
	/*
	public static synchronized UserDao getInstance(){
		//싱글톤 패턴을 단점
		//1. 생성자가 private 로 제한되어 있으므로 싱글톤 클래스 자신만이 자기 오브젝트를 만들도록 제한되어 있다.
		//2. 테스트가 힘들다.
		//3. 서버환경에서 싱글톤이 하나만 만들어지는 것을 보장하지 못한다.
		//4. 싱글톤의 사용은 전역상태를 만들수 있기 때문에 바람직하지 못하다.
		//이러한 작업을 스프링 레지스터리를 활용하여 스프링 컨테이너가 모두 해주기 때문에 편안하게 스프링컨테이너에 오브젝트을 생명주기를 맡겨 생성하도록 한다.
		

		if(INSTANCE == null)
			INSTANCE = new UserDao(???);
		return INSTANCE;
		
	}
	 */
	public void add(User user) throws SQLException, ClassNotFoundException {
		//Connection c = getConnection(); //메소드 분리 패턴
		//Connection c = simpleConnectionMaker.makeNewConnection(); //UserDao 가 DB 커넥션을 가져오는 정보를 너무 많이 알고있다
		this.c = connectionMaker.makeNewConnection();
		PreparedStatement ps = c.prepareStatement("insert into users(id,name,password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());

		ps.executeUpdate();
		ps.close();
		c.close();
	}
	
	public User get(String id) throws SQLException, ClassNotFoundException {
		this.c = connectionMaker.makeNewConnection();
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		this.user = new User();
		this.user.setId(rs.getString("id"));
		this.user.setName(rs.getString("name"));
		this.user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		return this.user;
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
