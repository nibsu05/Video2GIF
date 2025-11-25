package model.BO;

import java.util.List;
import model.Bean.VideoRequest;
import model.DAO.VideoRequestDAO;

public class VideoRequestBO {
    
    private VideoRequestDAO requestDAO;
    
    public VideoRequestBO() {
        this.requestDAO = new VideoRequestDAO();
    }
    
    // Tạo Request mới trong DB và trả về ID
    public int createVideoRequestOnly(VideoRequest request) {
        // 1. Lưu VideoRequest vào DB 
        int requestId = requestDAO.saveNewRequest(request);
        
        if (requestId > 0) {
            // Cập nhật request_id cho Bean
            request.setRequest_id(requestId); 
            return requestId;
        }
        return -1;
    }
    
    // Lấy danh sách yêu cầu cho Dashboard
    public List<VideoRequest> getRequestsForUser(Integer userId) {
        return requestDAO.getRequestsByUserId(userId);
    }
    
    // Lấy chi tiết yêu cầu
    public VideoRequest getRequestDetails(Integer requestId) {
    	return requestDAO.getRequestById(requestId);
    }
}