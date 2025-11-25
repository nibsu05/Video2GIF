package controller;

import java.io.File;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DAO.VideoRequestDAO;
import model.BO.VideoRequestBO;
import model.Bean.User;
import model.Bean.VideoRequest;

@WebServlet("/DeleteRequestServlet")
public class DeleteRequestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute("username");
        if (user == null) {
            response.sendRedirect("LoginServlet");
            return;
        }
        
        String requestIdStr = request.getParameter("request_id");
        if (requestIdStr == null) {
             response.sendRedirect("DashboardServlet?status=error&msg=MissingID");
             return;
        }

        try {
            Integer requestId = Integer.parseInt(requestIdStr);
            VideoRequestBO requestBO = new VideoRequestBO();
            VideoRequestDAO requestDAO = new VideoRequestDAO();
            
            // BỔ SUNG: 1. Kiểm tra quyền sở hữu (Quan trọng!)
            VideoRequest req = requestBO.getRequestDetails(requestId);
            if (req == null || !req.getUser_id().equals(user.getId())) {
                response.sendRedirect("DashboardServlet?status=error&msg=AccessDenied");
                return;
            }
            
            // Lấy đường dẫn file trước khi xóa bản ghi
            String videoPath = req.getVideo_path();
            String gifPath = req.getGif_path();

            // 2. Xóa bản ghi trong DB
            boolean dbSuccess = requestDAO.deleteRequest(requestId);
            
            if (dbSuccess) {
                // BỔ SUNG: 3. Xóa file vật lý (video gốc và GIF)
                String appPath = "E:\\Video2GIF_Data";
                
                // Xóa file video gốc
                if (videoPath != null && !videoPath.isEmpty()) {
                    File videoFile = new File(appPath + File.separator + videoPath.replace("/", File.separator));
                    if (videoFile.exists()) {
                        if (!videoFile.delete()) {
                            System.err.println("Không thể xóa file video: " + videoFile.getAbsolutePath());
                        }
                    }
                }
                
                // Xóa file GIF (nếu đã tạo)
                if (gifPath != null && !gifPath.isEmpty()) {
                    File gifFile = new File(appPath + File.separator + gifPath.replace("/", File.separator));
                    if (gifFile.exists()) {
                        if (!gifFile.delete()) {
                            System.err.println("Không thể xóa file GIF: " + gifFile.getAbsolutePath());
                        }
                    }
                }
                
                response.sendRedirect("DashboardServlet?status=delete_success");
            } else {
                response.sendRedirect("DashboardServlet?status=error&msg=DeleteFailed");
            }
            
        } catch (NumberFormatException e) {
             response.sendRedirect("DashboardServlet?status=error&msg=InvalidID");
        }
	}
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}