package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NUserDao {
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/toby","sa",""); 
		return c;
	}

}
