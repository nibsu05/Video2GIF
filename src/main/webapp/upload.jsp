<%@page import="model.Bean.User" %>
    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
        <% User user=(User) request.getSession().getAttribute("username"); if (user==null) {
            response.sendRedirect("LoginServlet"); return; } %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8">
                <title>Upload Video - Video2GIF</title>
                <link rel="stylesheet" href="css/style.css">
            </head>

            <body>

                <div class="header">
                    <div class="container">
                        <div class="logo">Video2GIF</div>
                        <div class="nav-links">
                            <a href="DashboardServlet">Dashboard</a>
                            <a href="LogoutServlet" class="btn btn-danger">Đăng xuất</a>
                        </div>
                    </div>
                </div>

                <div class="container main-content">
                    <div class="card">
                        <h1>Upload Video</h1>
                        <p style="text-align: center; color: var(--text-muted); margin-bottom: 20px;">
                            Chọn video và khoảng thời gian bạn muốn dùng để tạo GIF
                        </p>

                        <form id="uploadForm" action="UploadServlet" method="post" enctype="multipart/form-data">
                            <div class="form-group">
                                <label>Chọn Video</label>
                                <input type="file" id="videoFile" name="videoFile" accept="video/*" required>
                            </div>

                            <div class="form-group">
                                <label>Thời gian bắt đầu (MM:SS)</label>
                                <input type="text" name="start_time" value="00:00" pattern="[0-5]?[0-9]:[0-5][0-9]"
                                    title="Format: MM:SS" required placeholder="Ví dụ: 00:05">
                            </div>

                            <div class="form-group">
                                <label>Thời gian kết thúc (MM:SS)</label>
                                <input type="text" name="end_time" value="00:10" pattern="[0-5]?[0-9]:[0-5][0-9]"
                                    title="Format: MM:SS" required placeholder="Ví dụ: 00:15">
                            </div>

                            <div style="display: flex; gap: 10px; margin-top: 20px;">
                                <input type="submit" value="Tạo GIF" class="btn btn-block">
                                <input type="reset" value="Làm mới" class="btn btn-secondary btn-block">
                            </div>
                        </form>

                        <div style="text-align: center; margin-top: 20px;">
                            <a href="DashboardServlet">Quay lại Dashboard</a>
                        </div>
                    </div>
                </div>

                <div class="footer">
                    &copy; 2025 Video2GIF Project. All rights reserved.
                </div>
                
                <script>
        document.getElementById('uploadForm').addEventListener('submit', function(event) {
            const fileInput = document.getElementById('videoFile');
            
            if (fileInput.files.length === 0) {
                alert('Vui lòng chọn file video.');
                event.preventDefault();
                return;
            }
            
            const file = fileInput.files[0];
            const MAX_SIZE = 100 * 1024 * 1024; 
            
            if (file.size > MAX_SIZE) {
                alert('Lỗi: Kích thước tệp tin (' + (file.size / (1024 * 1024)).toFixed(2) + ' MB) vượt quá giới hạn 100MB. Vui lòng chọn tệp nhỏ hơn.');
                event.preventDefault();
            }
        });
    </script>

            </body>

            </html>