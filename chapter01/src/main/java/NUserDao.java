

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NUserDao extends UserDao{
	//템플릿 메소드 패턴
	//서브클래스에서 메소드내용을 구현한다
	@Override
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/toby","sa",""); 
		return c;
	}

	

}
