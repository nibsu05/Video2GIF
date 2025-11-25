package model.BO;

import java.io.File;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.http.Part;
import model.Bean.VideoRequest;
import model.DAO.VideoRequestDAO;
import utils.ApplicationConfig;

public class VideoRequestBO {
    
    private VideoRequestDAO requestDAO;
    
    public VideoRequestBO() {
        this.requestDAO = new VideoRequestDAO();
    }
    
    public List<VideoRequest> getRequestsForUser(Integer userId) {
        return requestDAO.getRequestsByUserId(userId);
    }
    
    public VideoRequest getRequestDetails(Integer requestId) {
    	return requestDAO.getRequestById(requestId);
    }

    public boolean deleteRequestAndFiles(Integer requestId, Integer userId) throws Exception {
        VideoRequest req = getRequestDetails(requestId);
        if (req == null || !req.getUser_id().equals(userId)) {
            throw new SecurityException("Access Denied: Không có quyền xóa yêu cầu này.");
        }
        
        boolean dbSuccess = requestDAO.deleteRequest(requestId);
        
        if (dbSuccess) {
            String baseDir = ApplicationConfig.BASE_DATA_PATH;
            
            if (req.getVideo_path() != null && !req.getVideo_path().isEmpty()) {
                File videoFile = new File(baseDir + File.separator + req.getVideo_path().replace("/", File.separator));
                if (videoFile.exists()) {
                    if (!videoFile.delete()) {
                        System.err.println("Không thể xóa file video: " + videoFile.getAbsolutePath());
                    }
                }
            }
            
            if (req.getGif_path() != null && !req.getGif_path().isEmpty()) {
                File gifFile = new File(baseDir + File.separator + req.getGif_path().replace("/", File.separator));
                if (gifFile.exists()) {
                    if (!gifFile.delete()) {
                        System.err.println("Không thể xóa file GIF: " + gifFile.getAbsolutePath());
                    }
                }
            }
            return true;
        } else {
            throw new Exception("Lỗi khi xóa Request khỏi CSDL.");
        }
    }
}