package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Bean.User;
import utils.PasswordUtil;

public class UserDAO extends BaseDAO {

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("email")
        );
    }

    public User authenticateUser(String username, String plainPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    if (PasswordUtil.checkPassword(plainPassword, user.getPassword())) {
                        return user;
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
    

    public boolean registerNewUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        try (Connection cnn = getConnection();
             PreparedStatement ps = cnn.prepareStatement(sql)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}