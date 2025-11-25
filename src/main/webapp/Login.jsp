<%@page import="model.Bean.User" %>
    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Đăng nhập - Video2GIF</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body>

            <div class="header">
                <div class="container">
                    <div class="logo">Video2GIF</div>
                    <div class="nav-links">
                        <a href="index.jsp">Trang chủ</a>
                        <a href="RegisterServlet" class="btn btn-secondary">Đăng ký</a>
                    </div>
                </div>
            </div>

            <div class="container main-content">
                <div class="card">
                    <h1>Đăng nhập</h1>

                    <% String status=request.getParameter("status"); String error=request.getParameter("error"); if
                        (status !=null && status.equals("register_success")) { %>
                        <div class="message success">Đăng ký thành công! Vui lòng đăng nhập.</div>
                        <% } if (error !=null && error.equals("invalid")) { %>
                            <div class="message error">Tên đăng nhập hoặc mật khẩu không đúng.</div>
                            <% } %>

                                <form action="CheckInfoServlet" method="post">
                                    <div class="form-group">
                                        <label>Tài khoản</label>
                                        <input name="username" type="text" required placeholder="Nhập tên đăng nhập">
                                    </div>
                                    <div class="form-group">
                                        <label>Mật khẩu</label>
                                        <input name="password" type="password" required placeholder="Nhập mật khẩu">
                                    </div>
                                    <button type="submit" class="btn btn-block">Đăng nhập</button>
                                </form>

                                <div style="text-align: center; margin-top: 15px;">
                                    Chưa có tài khoản? <a href="RegisterServlet" style="font-size: 14px;">Đăng ký
                                        ngay</a>
                                </div>
                </div>
            </div>

            <div class="footer">
                &copy; 2025 Video2GIF Project. All rights reserved.
            </div>

        </body>

        </html>