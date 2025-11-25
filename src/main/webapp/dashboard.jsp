<%@page import="model.Bean.VideoRequest" %>
    <%@page import="model.Bean.User" %>
        <%@page import="java.util.List" %>
            <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
                <!DOCTYPE html>
                <html>

                <head>
                    <meta charset="UTF-8">
                    <title>Dashboard - Video2GIF</title>
                    <link rel="stylesheet" href="css/style.css">
                </head>

                <body>

                    <% User user=(User)request.getSession().getAttribute("username"); List<VideoRequest> requests =
                        (List<VideoRequest>) request.getAttribute("videoRequests");

                            String statusParam = request.getParameter("status");
                            String msgParam = request.getParameter("msg");

                            if (user != null) {
                            %>

                            <div class="header">
                                <div class="container">
                                    <div class="logo">Video2GIF</div>
                                    <div class="nav-links">
                                        <span>Xin chào, <b>
                                                <%=user.getUsername()%>
                                            </b></span>
                                        <a href="LogoutServlet" class="btn btn-danger" style="margin-left: 15px;">Đăng
                                            xuất</a>
                                    </div>
                                </div>
                            </div>

                            <div class="container">

                                <% if (statusParam !=null) { String message="" ; String cssClass="message " ; switch
                                    (statusParam) { case "register_success" :
                                    message="Đăng ký thành công! Bạn đã được tự động đăng nhập." ; cssClass +="success"
                                    ; break; case "upload_success" :
                                    message="Yêu cầu upload và xử lý đã được gửi thành công. Vui lòng chờ đợi." ;
                                    cssClass +="success" ; break; case "delete_success" :
                                    message="Yêu cầu và các file liên quan đã được xóa thành công." ; cssClass
                                    +="success" ; break; case "error" : message="Đã xảy ra lỗi: " + (msgParam !=null ?
                                    msgParam : "Lỗi không xác định" ); cssClass +="error" ; break; } if
                                    (!message.isEmpty()) { %>
                                    <div class="<%=cssClass%>">
                                        <%=message%>
                                    </div>
                                    <% } } %>

                                        <div class="card wide">
                                            <div
                                                style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                                                <h2 style="margin: 0; color: var(--text-color);">Danh sách Video</h2>
                                                <a href="UploadServlet" class="btn">(+) Upload Video Mới</a>
                                            </div>

                                            <table cellspacing="0">
                                                <thead>
                                                    <tr>
                                                        <th>STT</th>
                                                        <th>Tên video</th>
                                                        <th>Thời gian cắt</th>
                                                        <th>Trạng thái</th>
                                                        <th>Hành động</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <% if (requests !=null && !requests.isEmpty()) { int stt=1; for
                                                        (VideoRequest req : requests) { String statusClass="" ; switch
                                                        (req.getStatus()) { case "PENDING" :
                                                        statusClass="status-pending" ; break; case "PROCESSING" :
                                                        statusClass="status-processing" ; break; case "COMPLETED" :
                                                        statusClass="status-completed" ; break; case "FAILED" :
                                                        statusClass="status-failed" ; break; } %>
                                                        <tr>
                                                            <td>
                                                                <%=stt++%>
                                                            </td>
                                                            <td>
                                                                <div style="font-weight: bold; margin-bottom: 5px;"
                                                                    title="<%=req.getOriginal_video_name()%>">
                                                                    <%=req.getOriginal_video_name()%>
                                                                </div>
                                                                <% if ("COMPLETED".equals(req.getStatus())) { %>
                                                                    <img src="PreviewServlet?request_id=<%=req.getRequest_id()%>"
                                                                        alt="GIF Preview"
                                                                        style="max-width: 150px; border-radius: 4px; border: 1px solid #ddd;">
                                                                    <% } %>
                                                            </td>
                                                            <td>
                                                                <%=req.getStart_time()%> - <%=req.getEnd_time()%>
                                                            </td>
                                                            <td><span class="status <%=statusClass%>">
                                                                    <%=req.getStatus()%>
                                                                </span></td>
                                                            <td>
                                                                <div style="display: flex; gap: 5px; flex-wrap: wrap;">
                                                                    <% if ("COMPLETED".equals(req.getStatus())) { %>
                                                                        <a href="PreviewServlet?request_id=<%=req.getRequest_id()%>"
                                                                            target="_blank" class="btn"
                                                                            style="padding: 5px 10px; font-size: 12px;">Xem</a>
                                                                        <a href="DownloadServlet?request_id=<%=req.getRequest_id()%>"
                                                                            class="btn btn-secondary"
                                                                            style="padding: 5px 10px; font-size: 12px;">Tải
                                                                            về</a>
                                                                        <% } %>
                                                                            <a href="DeleteRequestServlet?request_id=<%=req.getRequest_id()%>"
                                                                                onclick="return confirm('Bạn có chắc chắn muốn xóa yêu cầu #<%=req.getRequest_id()%>?');"
                                                                                class="btn btn-danger"
                                                                                style="padding: 5px 10px; font-size: 12px;">Xóa</a>
                                                                </div>

                                                                <% if ("FAILED".equals(req.getStatus())) { %>
                                                                    <div
                                                                        style="color: red; font-size: 11px; margin-top: 5px;">
                                                                        Lỗi xử lý</div>
                                                                    <% } %>
                                                            </td>
                                                        </tr>
                                                        <% } } else { %>
                                                            <tr>
                                                                <td colspan="5"
                                                                    style="text-align: center; color: var(--text-muted);">
                                                                    Bạn chưa có yêu cầu chuyển đổi nào. Hãy upload video
                                                                    mới!</td>
                                                            </tr>
                                                            <% } %>
                                                </tbody>
                                            </table>
                                        </div>
                            </div>

                            <div class="footer">
                                &copy; 2025 Video2GIF Project. All rights reserved.
                            </div>

                            <% } else { response.sendRedirect("LoginServlet"); } %>

                </body>

                </html>