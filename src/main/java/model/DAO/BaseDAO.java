package model.DAO;

import java.sql.Connection;
import utils.DBUtil;

public abstract class BaseDAO {
    public Connection getConnection() {
        return DBUtil.getConnection();
    }
}