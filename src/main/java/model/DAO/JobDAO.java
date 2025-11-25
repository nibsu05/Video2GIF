package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import model.Bean.Job;

public class JobDAO extends BaseDAO { 

    public int addJobToQueue(int requestId) {
        String sql = "INSERT INTO jobs (request_id) VALUES (?)";
        try (Connection cnn = getConnection();
             PreparedStatement ps = cnn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, requestId);
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Trả về job_id
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Phương thức này không cần thiết trong mô hình TCP Push Queue, 
     * nhưng có thể được dùng cho Worker cũ hoặc cho mục đích debug/theo dõi.
     */
    public Job getPendingJob() {
        // Logic tìm kiếm job pending (không cần thiết trong mô hình TCP)
        return null;
    }
    
    /**
     * Cập nhật trạng thái Job (dùng để log trạng thái xử lý)
     */
    public boolean updateJobStatus(Integer jobId, String status) {
        String sql = "UPDATE jobs SET status = ? WHERE job_id = ?";
        try (Connection cnn = getConnection();
             PreparedStatement ps = cnn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, jobId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}