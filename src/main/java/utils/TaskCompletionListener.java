package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.DAO.VideoRequestDAO;
import model.Bean.WorkerResponse;


public class TaskCompletionListener implements Runnable {
    
    private static final Logger LOGGER = Logger.getLogger(TaskCompletionListener.class.getName());
    private static final int SERVER_PORT = 9998; // Server lắng nghe Worker ở cổng này
    private volatile boolean running = true;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5); // Xử lý 5 thông báo đồng thời
    private ServerSocket serverSocket; // Bổ sung ServerSocket để có thể đóng nó

    public void stop() {
        this.running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close(); // Đóng socket để ngắt chặn .accept()
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Lỗi đóng ServerSocket: " + e.getMessage());
            }
        }
        threadPool.shutdownNow();
        LOGGER.log(Level.INFO, "Đã yêu cầu TaskCompletionListener dừng.");
    }
    
    @Override
    public void run() {
        LOGGER.log(Level.INFO, "TaskCompletionListener khởi động. Lắng nghe tại cổng {0}...", SERVER_PORT);
        try {
             serverSocket = new ServerSocket(SERVER_PORT); // Khởi tạo ServerSocket
            while (running) {
                try {
                    // Chấp nhận kết nối từ Worker
                    Socket workerSocket = serverSocket.accept();
                    // Giao cho Thread Pool xử lý thông báo
                    threadPool.submit(new WorkerHandler(workerSocket));
                } catch (IOException e) {
                    if (running) { // Chỉ log lỗi nếu server không bị dừng thủ công
                        LOGGER.log(Level.WARNING, "Lỗi khi chấp nhận kết nối Worker: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khởi tạo ServerSocket: " + e.getMessage());
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                 try { serverSocket.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    // Lớp nội bộ để xử lý từng kết nối Worker riêng lẻ
    private class WorkerHandler implements Runnable {
        private Socket workerSocket;
        // SỬA: Dùng DAO để cập nhật DB
        private VideoRequestDAO requestDAO = new VideoRequestDAO(); 

        public WorkerHandler(Socket socket) {
            this.workerSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream ois = new ObjectInputStream(workerSocket.getInputStream())) {
                
                // Đọc phản hồi (WorkerResponse) từ Worker
                // ĐÃ SỬA LỖI: Worker gửi đối tượng WorkerResponse thay vì VideoWorker
                WorkerResponse response = (WorkerResponse) ois.readObject();
                
                int requestId = response.getRequestId();
                String status = response.getStatus();
                String gifPath = response.getGifPath();
                String errorMessage = response.getErrorMessage(); // Bổ sung

                LOGGER.log(Level.INFO, "Nhận phản hồi từ Worker: RequestID={0}, Status={1}, GIF Path={2}, Error={3}", 
                           new Object[]{requestId, status, gifPath, errorMessage});

                // Cập nhật DB (COMPLETED: cập nhật status và gifPath; FAILED: cập nhật status và ghi log lỗi)
                if ("COMPLETED".equals(status)) {
                    requestDAO.updateRequestStatusAndGifPath(requestId, status, gifPath);
                } else if ("FAILED".equals(status)) {
                    // Nếu thất bại, ta có thể cập nhật status và xóa gifPath (đảm bảo nó là NULL)
                    requestDAO.updateRequestStatusAndGifPath(requestId, status, null);
                    LOGGER.log(Level.WARNING, "Task FAILED cho RequestID #{0}. Chi tiết lỗi: {1}", 
                               new Object[]{requestId, errorMessage != null ? errorMessage : "Không có chi tiết lỗi"});
                }
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi xử lý thông báo Worker: " + e.getMessage(), e);
            } finally {
                try {
                    workerSocket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}