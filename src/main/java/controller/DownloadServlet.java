package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BO.VideoRequestBO;
import model.Bean.VideoRequest;
import model.Bean.User;
import utils.ApplicationConfig;

@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing request_id.");
            return;
        }
        
        try {
            Integer requestId = Integer.parseInt(requestIdStr);
            VideoRequestBO requestBO = new VideoRequestBO();
            VideoRequest req = requestBO.getRequestDetails(requestId);
            
            if (req == null || !req.getStatus().equals("COMPLETED")) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found or not completed.");
                return;
            }
            
            if (!req.getUser_id().equals(user.getId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied. You do not own this file.");
                return;
            }
            
            String relativeGifPath = req.getGif_path(); 
            String baseDir = ApplicationConfig.BASE_DATA_PATH;
            String gifPath = baseDir + File.separator + relativeGifPath.replace("/", File.separator);
            
            File gifFile = new File(gifPath);
            if (!gifFile.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "GIF file not found on server.");
                return;
            }

            response.setContentType("image/gif");
            response.setContentLength((int) gifFile.length());
            
            String downloadFileName = req.getOriginal_video_name().replaceAll("[^a-zA-Z0-9.-]", "_").replaceFirst("\\.[^\\.]+$", ".gif");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"");
            
            try (FileInputStream in = new FileInputStream(gifFile)) {
                in.transferTo(response.getOutputStream());
            }
            
        } catch (NumberFormatException e) {
             response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request_id format.");
        } catch (Exception e) {
            System.err.println("Lỗi xảy ra trong quá trình tải xuống: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during download.");
        }
	}
}