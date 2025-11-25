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
    private volatile boolean running = true;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5); 
    private ServerSocket serverSocket; 

    public void stop() {
        this.running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Lỗi đóng ServerSocket: " + e.getMessage());
            }
        }
        threadPool.shutdownNow();
        LOGGER.log(Level.INFO, "Đã yêu cầu TaskCompletionListener dừng.");
    }
    
    @Override
    public void run() {
        LOGGER.log(Level.INFO, "TaskCompletionListener khởi động. Lắng nghe tại cổng {0}...", 
                   ApplicationConfig.COMPLETION_LISTENER_PORT);
        try {
             serverSocket = new ServerSocket(ApplicationConfig.COMPLETION_LISTENER_PORT); 
            while (running) {
                try {
                    Socket workerSocket = serverSocket.accept();
                    threadPool.submit(new WorkerHandler(workerSocket));
                } catch (IOException e) {
                    if (running) { 
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

    private class WorkerHandler implements Runnable {
        private Socket workerSocket;
        private VideoRequestDAO requestDAO = new VideoRequestDAO(); 

        public WorkerHandler(Socket socket) {
            this.workerSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream ois = new ObjectInputStream(workerSocket.getInputStream())) {
                
                WorkerResponse response = (WorkerResponse) ois.readObject();
                
                int requestId = response.getRequestId();
                String status = response.getStatus();
                String gifPath = response.getGifPath();
                String errorMessage = response.getErrorMessage();

                LOGGER.log(Level.INFO, "Nhận phản hồi từ Worker: RequestID={0}, Status={1}, Error={2}", 
                           new Object[]{requestId, status, errorMessage});

                if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
                    requestDAO.updateRequestStatusAndGifPath(requestId, status, gifPath);
                    if ("FAILED".equals(status)) {
                        LOGGER.log(Level.WARNING, "Task FAILED cho RequestID #{0}. Chi tiết lỗi: {1}", 
                                   new Object[]{requestId, errorMessage != null ? errorMessage : "Không có chi tiết lỗi"});
                    }
                } else {
                     LOGGER.log(Level.WARNING, "Nhận trạng thái không hợp lệ: " + status);
                }
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi xử lý thông báo Worker: " + e.getMessage(), e);
            } finally {
                try {
                    if (!workerSocket.isClosed()) workerSocket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}