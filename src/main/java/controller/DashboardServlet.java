package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.BO.VideoRequestBO;
import model.Bean.User;
import model.Bean.VideoRequest;
import java.util.List;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute("username");
        
        if (user == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        VideoRequestBO requestBO = new VideoRequestBO();
        
        // 1. Lấy danh sách các yêu cầu của người dùng
        List<VideoRequest> requests = requestBO.getRequestsForUser(user.getId());
        
        // 2. Đặt vào request attribute và chuyển tiếp
        request.setAttribute("videoRequests", requests);
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}