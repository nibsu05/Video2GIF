package utils;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Bean.TaskData;
import model.Bean.VideoRequest;

public class TCPTaskSender {
    private static final String WORKER_HOST = "localhost"; // Địa chỉ Worker
    private static final int WORKER_PORT = 9999;           // Cổng Worker lắng nghe

    private static final Logger LOGGER = Logger.getLogger(TCPTaskSender.class.getName());

    public static boolean sendTask(VideoRequest request, String absoluteVideoPath) {
        TaskData task = new TaskData(
            request.getRequest_id(),
            absoluteVideoPath,
            request.getStart_time(),
            request.getEnd_time()
        );

        // Trong mô hình này, chúng ta giả định Worker luôn sẵn sàng tại Host/Port này.
        // Trong thực tế, cần một Thread Pool chứa các Worker Socket.
        try (Socket socket = new Socket(WORKER_HOST, WORKER_PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            
            oos.writeObject(task);
            oos.flush();
            LOGGER.log(Level.INFO, "Đã gửi Task #{0} đến Worker {1}:{2}", new Object[]{request.getRequest_id(), WORKER_HOST, WORKER_PORT});
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Không thể gửi Task #{0} qua TCP. Lỗi: {1}", new Object[]{request.getRequest_id(), e.getMessage()});
            e.printStackTrace();
            return false;
        }
    }
}