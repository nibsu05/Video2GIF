package model.BO;

import java.io.File;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.http.Part;
import model.Bean.VideoRequest;
import model.DAO.VideoRequestDAO;
import utils.ApplicationConfig;
import utils.TCPTaskSender;

public class VideoRequestBO {
    
    private VideoRequestDAO requestDAO;
    
    public VideoRequestBO() {
        this.requestDAO = new VideoRequestDAO();
    }
    

    public boolean processUploadAndSendTask(Integer userId, Part filePart, String startTime, String endTime) throws Exception {
        String originalFileName = filePart.getSubmittedFileName();
        String uniqueFileName = userId + "_" + System.currentTimeMillis() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String relativeFilePath = ApplicationConfig.UPLOAD_DIR + "/" + uniqueFileName;
        String absoluteFilePath = ApplicationConfig.getUploadDirAbsolutePath() + File.separator + uniqueFileName;
        
        File fileToDelete = null;
        int requestId = -1;
        
        try {
            filePart.write(absoluteFilePath);
            fileToDelete = new File(absoluteFilePath);

            VideoRequest requestBean = new VideoRequest();
            requestBean.setUser_id(userId);
            requestBean.setOriginal_video_name(originalFileName);
            requestBean.setVideo_path(relativeFilePath); 
            requestBean.setStatus("PENDING"); 
            requestBean.setStart_time(startTime);
            requestBean.setEnd_time(endTime);
            
            requestId = requestDAO.saveNewRequest(requestBean);

            if (requestId > 0) {
                requestBean.setRequest_id(requestId);
                
                boolean taskSent = TCPTaskSender.sendTask(requestBean, relativeFilePath);
                
                if (taskSent) {
                    return true;
                } else {
                    requestDAO.deleteRequest(requestId); 
                    if (fileToDelete.exists()) {
                        fileToDelete.delete();
                    }
                    throw new IOException("Không thể gửi task đến Worker.");
                }
            } else {
                throw new Exception("Lỗi khi lưu Request vào CSDL.");
            }
        } catch (Exception e) {
            if (requestId > 0) {
                 requestDAO.deleteRequest(requestId);
            }
            if (fileToDelete != null && fileToDelete.exists()) {
                fileToDelete.delete();
            }
            throw e;
        }
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