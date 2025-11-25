# Hướng dẫn Cài đặt và Sử dụng Video2GIF

Chào mừng bạn đến với dự án **Video2GIF** - Ứng dụng web chuyển đổi Video sang ảnh động GIF.

---

## 1. Yêu cầu Hệ thống

Để chạy được ứng dụng này, máy tính của bạn cần cài đặt các phần mềm sau:

*   **Java Development Kit (JDK)**: Phiên bản 17 trở lên.
*   **Apache Tomcat**: Phiên bản 10.0 trở lên (Hỗ trợ Jakarta EE 9+).
*   **MySQL Server**: Phiên bản 8.0 trở lên.
*   **FFmpeg**: Công cụ xử lý video. Cần cài đặt và **thêm vào biến môi trường (PATH)** của hệ thống để ứng dụng có thể gọi lệnh `ffmpeg`.
*   **Eclipse IDE for Enterprise Java and Web Developers**: Để mở và chạy dự án.

---

## 2. Hướng dẫn Cài đặt Database

1.  Mở công cụ quản lý MySQL (như MySQL Workbench, phpMyAdmin, hoặc Command Line).
2.  Tạo một database mới có tên là `dut-video2gif`.
3.  Chạy đoạn script SQL dưới đây để tạo các bảng cần thiết:

```sql
CREATE DATABASE IF NOT EXISTS `dut-video2gif`;
USE `dut-video2gif`;

-- Bảng người dùng
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bảng yêu cầu video
CREATE TABLE IF NOT EXISTS `video_requests` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `original_video_name` varchar(255) DEFAULT NULL,
  `video_path` varchar(500) DEFAULT NULL,
  `gif_path` varchar(500) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'PENDING',
  `start_time` varchar(10) DEFAULT '00:00',
  `end_time` varchar(10) DEFAULT '00:10',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`request_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `video_requests_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

4.  **Lưu ý cấu hình kết nối:**
    *   Mặc định ứng dụng kết nối với:
        *   URL: `jdbc:mysql://localhost:3306/dut-video2gif`
        *   User: `root`
        *   Pass: `123456`
    *   Nếu cấu hình MySQL của bạn khác, hãy sửa file `src/main/java/utils/ApplicationConfig.java`.

---

## 3. Hướng dẫn Cài đặt và Chạy Server (Tomcat)

1.  **Import dự án vào Eclipse:**
    *   File -> Open Projects from File System... -> Chọn thư mục chứa dự án `Video2GIF`.
2.  **Cấu hình Server:**
    *   Trong tab **Servers**, click chuột phải -> New -> Server.
    *   Chọn **Apache Tomcat v10.0** (hoặc v10.1/v11.0).
    *   Trỏ đường dẫn đến thư mục cài đặt Tomcat trên máy bạn.
3.  **Thêm dự án vào Server:**
    *   Click chuột phải vào Server vừa tạo -> **Add and Remove...**
    *   Chọn `Video2GIF` và nhấn **Add >**, sau đó **Finish**.
4.  **Chuẩn bị thư mục dữ liệu:**
    *   Tạo thư mục `C:\Users\Admin\Video2GIF_Data` trên máy tính của bạn. Đây là nơi lưu trữ video upload và file GIF đầu ra.
5.  **Chạy Server:**
    *   Nhấn nút **Start** (biểu tượng Play màu xanh) trong tab Servers.
    *   Truy cập trình duyệt tại: `http://localhost:8080/Video2GIF`

---

## 4. Hướng dẫn Chạy Video Worker (Quan trọng)

Ứng dụng gồm 2 phần: **Web Server** (Tomcat) và **Video Worker** (Xử lý ngầm). Bạn cần chạy Worker để video được chuyển đổi.

1.  Tìm file `run_worker.bat` trong thư mục gốc của dự án.
2.  Click đúp để chạy file này.
3.  Một cửa sổ dòng lệnh (CMD) sẽ hiện ra với thông báo `--- VideoWorker started...`. **Đừng tắt cửa sổ này** trong quá trình sử dụng.

---

## 5. Hướng dẫn Sử dụng

### A. Đăng ký / Đăng nhập
1.  Tại trang chủ, nhấn **Đăng ký** để tạo tài khoản mới.
2.  Nhập thông tin và nhấn nút Đăng ký.
3.  Sau khi đăng ký thành công, đăng nhập bằng tài khoản vừa tạo.

### B. Upload Video và Tạo GIF
1.  Sau khi đăng nhập, bạn sẽ được chuyển đến **Dashboard**.
2.  Nhấn nút **(+) Upload Video Mới**.
3.  Chọn file video (`.mp4`) từ máy tính.
4.  Nhập thời gian cắt:
    *   **Start Time**: Thời điểm bắt đầu (ví dụ: `00:05`).
    *   **End Time**: Thời điểm kết thúc (ví dụ: `00:10`).
5.  Nhấn **Tạo GIF**.

### C. Quản lý Yêu cầu (Dashboard)
1.  Sau khi upload, yêu cầu sẽ hiện trong danh sách với trạng thái **PENDING** (Đang chờ) hoặc **PROCESSING** (Đang xử lý).
2.  Khi Worker xử lý xong, trạng thái chuyển thành **COMPLETED** (Hoàn thành).
3.  **Xem trước**: Ảnh GIF sẽ hiện ngay dưới tên video. Bạn có thể nhấn nút **Xem** để mở to.
4.  **Tải về**: Nhấn nút **Tải về** để lưu ảnh GIF về máy.
5.  **Xóa**: Nhấn nút **Xóa** để xóa video và ảnh GIF khỏi hệ thống.

---

## 6. Xử lý sự cố thường gặp

*   **Lỗi "Upload Failed" hoặc "File Not Found":**
    *   Kiểm tra xem thư mục `C:\Users\Admin\Video2GIF_Data` đã được tạo chưa.
*   **Trạng thái mãi là PENDING:**
    *   Kiểm tra xem bạn đã chạy file `run_worker.bat` chưa.
*   **Lỗi FFmpeg:**
    *   Đảm bảo bạn đã cài FFmpeg và gõ được lệnh `ffmpeg -version` trong CMD.
*   **Lỗi Kết nối Database:**
    *   Đảm bảo MySQL Server đang chạy và mật khẩu trong `ApplicationConfig.java` là chính xác.
