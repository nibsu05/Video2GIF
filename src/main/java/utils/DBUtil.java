package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBUtil {
    
    private static final Logger LOGGER = Logger.getLogger(DBUtil.class.getName());
    
    public static Connection getConnection(){
        Connection cnn = null;

        try {
            Class.forName(ApplicationConfig.DB_DRIVER);
            cnn = DriverManager.getConnection(
                ApplicationConfig.DB_URL, 
                ApplicationConfig.DB_USER, 
                ApplicationConfig.DB_PASSWORD
            );
            return cnn;
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, "Không thể kết nối CSDL!", e);
        }
        return cnn;
    }
}