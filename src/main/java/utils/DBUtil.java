package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
	public DBUtil() {
		
	}
	
	public static Connection getConnection(){
		Connection cnn = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			cnn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dut-video2gif", "root", "123456");
			return cnn;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return cnn;
	}
}
