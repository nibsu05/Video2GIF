package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.BO.VideoRequestBO;
import model.Bean.User;

@WebServlet("/UploadServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 100, maxRequestSize = 1024 * 1024 * 150)
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute("username"); 
        if (user == null) {
            response.sendRedirect("LoginServlet"); 
            return;
        }

        try {
            Part filePart = request.getPart("videoFile"); 
            String startTime = request.getParameter("start_time"); 
            String endTime = request.getParameter("end_time");     

            VideoRequestBO requestBO = new VideoRequestBO();
            
            boolean success = requestBO.processUploadAndSendTask(user.getId(), filePart, startTime, endTime);

            if (success) {
                response.sendRedirect("DashboardServlet?status=upload_success"); 
            } else {
                response.sendRedirect("DashboardServlet?status=error&msg=ProcessFailed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            
            String errorMessage = "Lỗi không xác định.";
            if (e.getMessage() != null && (e.getMessage().contains("exceeds permitted size") || e.getMessage().contains("SizeLimitExceededException"))) {
                 errorMessage = "Kích thước tệp vượt quá giới hạn 100MB.";
            } else {
                 errorMessage = e.getMessage();
            }
            
            response.sendRedirect("DashboardServlet?status=error&msg=" + errorMessage);
    }
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("username") != null) {
            request.getRequestDispatcher("/upload.jsp").forward(request, response);
        } else {
            response.sendRedirect("LoginServlet");
        }
    }
}