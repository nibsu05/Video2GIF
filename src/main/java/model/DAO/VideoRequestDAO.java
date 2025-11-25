package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Bean.VideoRequest;

public class VideoRequestDAO extends BaseDAO {

    private VideoRequest mapResultSetToVideoRequest(ResultSet rs) throws SQLException {
        VideoRequest req = new VideoRequest();
        req.setRequest_id(rs.getInt("request_id"));
        req.setUser_id(rs.getInt("user_id"));
        req.setOriginal_video_name(rs.getString("original_video_name"));
        req.setVideo_path(rs.getString("video_path"));
        req.setGif_path(rs.getString("gif_path"));
        req.setStatus(rs.getString("status"));
        req.setStart_time(rs.getString("start_time"));
        req.setEnd_time(rs.getString("end_time"));
        return req;
    }

    public List<VideoRequest> getRequestsByUserId(Integer userId) {
        String sql = "SELECT * FROM video_requests WHERE user_id = ? ORDER BY created_at DESC";
        List<VideoRequest> list = new ArrayList<>();
        try (Connection cnn = getConnection();
             PreparedStatement ps = cnn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToVideoRequest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean updateRequestStatusAndGifPath(Integer requestId, String status, String gifPath) {
        String sql = "UPDATE video_requests SET status = ?, gif_path = ? WHERE request_id = ?";
        try (Connection cnn = getConnection();
             PreparedStatement ps = cnn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setString(2, gifPath);
            ps.setInt(3, requestId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public VideoRequest getRequestById(Integer requestId) {
        String sql = "SELECT * FROM video_requests WHERE request_id = ?";
        try (Connection cnn = getConnection();
             PreparedStatement ps = cnn.prepareStatement(sql)) {
            
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVideoRequest(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean deleteRequest(Integer requestId) {
    	String sql = "DELETE FROM video_requests WHERE request_id = ?";
        try (Connection cnn = getConnection();
             PreparedStatement ps = cnn.prepareStatement(sql)) {
            
            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}