<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <title>Đăng ký - Video2GIF</title>
        <link rel="stylesheet" href="css/style.css">
    </head>

    <body>

        <div class="header">
            <div class="container">
                <div class="logo">Video2GIF</div>
                <div class="nav-links">
                    <a href="index.jsp">Trang chủ</a>
                    <a href="LoginServlet" class="btn btn-secondary">Đăng nhập</a>
                </div>
            </div>
        </div>

        <div class="container main-content">
            <div class="card">
                <h1>Đăng ký</h1>

                <% String error=request.getParameter("error"); if (error !=null) { String msg="" ; if
                    (error.equals("mismatch")) { msg="Mật khẩu xác nhận không khớp." ; } else if
                    (error.equals("usertaken")) { msg="Tên đăng nhập đã được sử dụng." ; } else {
                    msg="Đã xảy ra lỗi trong quá trình đăng ký." ; } %>
                    <div class="message error">
                        <%=msg%>
                    </div>
                    <% } %>

                        <form action="RegisterServlet" method="post">
                            <div class="form-group">
                                <label>Tài khoản</label>
                                <input name="username" type="text" required placeholder="Chọn tên đăng nhập">
                            </div>
                            <div class="form-group">
                                <label>Email</label>
                                <input name="email" type="email" required placeholder="Nhập địa chỉ email">
                            </div>
                            <div class="form-group">
                                <label>Mật khẩu</label>
                                <input name="password" type="password" required placeholder="Tạo mật khẩu">
                            </div>
                            <div class="form-group">
                                <label>Xác nhận mật khẩu</label>
                                <input name="confirm_password" type="password" required placeholder="Nhập lại mật khẩu">
                            </div>
                            <button type="submit" class="btn btn-block">Đăng ký</button>
                        </form>

                        <div style="text-align: center; margin-top: 15px;">
                            Đã có tài khoản?<a href="LoginServlet" style="font-size: 14px;"> Đăng nhập ngay</a>
                        </div>
            </div>
        </div>

        <div class="footer">
            &copy; 2025 Video2GIF Project. All rights reserved.
        </div>

    </body>

    </html>