package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.BO.UserBO;
import model.Bean.User;

@WebServlet("/CheckInfoServlet")
public class CheckInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CheckInfoServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("LoginServlet");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
        String username = request.getParameter("username");
		String password = request.getParameter("password");
		UserBO userBO = new UserBO();
        
		User user = userBO.login(username, password);
        
		if (user != null) {
			request.getSession().setAttribute("username", user); 
			response.sendRedirect("DashboardServlet");
		} else {
			response.sendRedirect("Login.jsp?error=invalid");
		}
	}

}