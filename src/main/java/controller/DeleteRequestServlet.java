package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BO.VideoRequestBO;
import model.Bean.User;

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
            
            boolean success = requestBO.deleteRequestAndFiles(requestId, user.getId());
            
            if (success) {
                response.sendRedirect("DashboardServlet?status=delete_success");
            } else {
                 response.sendRedirect("DashboardServlet?status=error&msg=DeleteFailedUnknown");
            }
            
        } catch (NumberFormatException e) {
             response.sendRedirect("DashboardServlet?status=error&msg=InvalidID");
        } catch (SecurityException e) {
             response.sendRedirect("DashboardServlet?status=error&msg=AccessDenied");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("DashboardServlet?status=error&msg=" + e.getMessage());
        }
	}
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}