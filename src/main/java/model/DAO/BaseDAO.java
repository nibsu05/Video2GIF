package model.DAO;

import java.sql.Connection;
import utils.DBUtil; // Giả định BaseDAO sử dụng tiện ích DBUtil

public abstract class BaseDAO {
    // Phương thức để lấy kết nối, có thể gọi DBUtil.getConnection()
    public Connection getConnection() {
        return DBUtil.getConnection();
    }
}
// Chú ý: Cần điều chỉnh lại `UserDAO.java` và `VideoRequestDAO.java` để kế thừa lớp này.