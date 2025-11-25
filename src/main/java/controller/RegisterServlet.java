package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.BO.UserBO;
import model.Bean.User;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("Register.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
        String username = request.getParameter("username");
		String password = request.getParameter("password");
        String email = request.getParameter("email");
        String confirmPassword = request.getParameter("confirm_password");
        
        // 1. Kiểm tra mật khẩu khớp
        if (!password.equals(confirmPassword)) {
            response.sendRedirect("RegisterServlet?error=mismatch");
            return;
        }

		UserBO userBO = new UserBO();
        
        // 2. Thực hiện đăng ký (BO sẽ hash password và kiểm tra username)
		boolean success = userBO.registerNewUser(username, password, email);
        
		if (success) {
            // Đăng ký thành công, chuyển hướng đến trang Đăng nhập
            // Đặt User object vào session với key THỐNG NHẤT là "username"
            // (Tuỳ chọn: có thể đăng nhập luôn, ở đây ta chuyển về Login)
            User user = userBO.login(username, password);
            if(user != null) {
                request.getSession().setAttribute("username", user);
                response.sendRedirect("DashboardServlet?status=register_success");
                return;
            } else {
                // Đăng ký thành công nhưng đăng nhập tự động thất bại (rất hiếm)
                response.sendRedirect("LoginServlet?status=register_success");
                return;
            }
		} else {
            // Đăng ký thất bại (có thể do username đã tồn tại)
			response.sendRedirect("RegisterServlet?error=usertaken");
		}
	}
}