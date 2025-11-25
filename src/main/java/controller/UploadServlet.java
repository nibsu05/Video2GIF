package controller;

import java.io.File;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.BO.VideoRequestBO;
import model.Bean.VideoRequest;
import model.Bean.User;
import utils.TCPTaskSender;

@WebServlet("/UploadServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 100, maxRequestSize = 1024 * 1024
        * 150)
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String SAVE_DIR = "uploaded_videos";

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

            String originalFileName = filePart.getSubmittedFileName();
            System.out.println("DEBUG: User ID = " + user.getId());
            System.out.println("DEBUG: File Name = " + filePart.getSubmittedFileName());
            System.out.println("DEBUG: File Size = " + filePart.getSize());
            // 2. Lưu file lên server
            String appPath = "C:\\Users\\Admin\\Video2GIF_Data";
            String savePath = appPath + File.separator + SAVE_DIR;
            System.out.println("DEBUG: App Path = " + savePath);
            File fileSaveDir = new File(savePath);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }

            // Tạo tên file duy nhất và đường dẫn vật lý
            // Dùng ID người dùng để tránh xung đột trong môi trường multi-user
            String uniqueFileName = user.getId() + "_" + System.currentTimeMillis() + "_"
                    + originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
            String absoluteFilePath = savePath + File.separator + uniqueFileName;
            filePart.write(absoluteFilePath);

            // 3. Tạo Request trong DB
            VideoRequest requestBean = new VideoRequest();
            requestBean.setUser_id(user.getId());
            requestBean.setOriginal_video_name(originalFileName);
            // Lưu đường dẫn tương đối (sử dụng dấu "/" để dễ xử lý trong DB và JSP)
            requestBean.setVideo_path(SAVE_DIR + "/" + uniqueFileName);
            requestBean.setStatus("PENDING"); // Trạng thái ban đầu
            requestBean.setStart_time(startTime);
            requestBean.setEnd_time(endTime);

            VideoRequestBO requestBO = new VideoRequestBO();
            int requestId = requestBO.createVideoRequestOnly(requestBean); // Tạo request DB

            if (requestId > 0) {
                boolean taskSent = TCPTaskSender.sendTask(requestBean, requestBean.getVideo_path());
                System.out.println("DEBUG: Task sent to Worker? " + taskSent);
                if (taskSent) {
                    response.sendRedirect("DashboardServlet?status=upload_success");
                } else {
                    // Nếu gửi task thất bại, xóa file vật lý và bản ghi DB là cần thiết để tránh
                    // lãng phí.
                    // Để đơn giản, ta chỉ báo lỗi và giữ request ở trạng thái PENDING.
                    // Trong ứng dụng thực tế, nên cung cấp cách re-queue task.
                    response.sendRedirect("DashboardServlet?status=error&msg=TaskSendFailed");
                }
            } else {
                // Lỗi DB khi tạo Request
                response.sendRedirect("DashboardServlet?status=error&msg=DatabaseError");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("DashboardServlet?status=error&msg=UploadFailed");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession().getAttribute("username") != null) {
            request.getRequestDispatcher("/upload.jsp").forward(request, response);
        } else {
            response.sendRedirect("LoginServlet");
        }
    }
}