package worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import model.Bean.WorkerResponse;
import model.Bean.TaskData;
import utils.ApplicationConfig;

public class VideoWorker implements Runnable {

    private static final int SERVER_PORT = ApplicationConfig.TASK_SENDER_PORT;        
    private static final String SERVER_HOST = ApplicationConfig.WORKER_HOST;
    private static final int COMPLETION_PORT = ApplicationConfig.COMPLETION_LISTENER_PORT;       

    private static final int THREAD_POOL_SIZE = 4;
    private final ExecutorService taskPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private static String WEB_APP_ROOT = ApplicationConfig.BASE_DATA_PATH; 
    
    public VideoWorker() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            WEB_APP_ROOT = args[0]; 
        } else {
             System.err.println("LƯU Ý: Worker đang sử dụng đường dẫn mặc định: " + WEB_APP_ROOT);
        }
        
        if (!WEB_APP_ROOT.endsWith(File.separator) && !WEB_APP_ROOT.endsWith("/")) {
        	WEB_APP_ROOT += File.separator;
        }
        System.out.println("--- VideoWorker khởi động. Thư mục gốc Web App: " + WEB_APP_ROOT + " ---");
        new VideoWorker().run();
    }

    @Override
    public void run() {
        System.out.println("Worker lắng nghe các Task mới từ Server tại cổng " + SERVER_PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    taskPool.submit(new TaskProcessor(clientSocket));
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        System.err.println("Lỗi chấp nhận kết nối TCP: " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Không thể khởi tạo Server Socket cho Worker: " + e.getMessage());
        } finally {
            taskPool.shutdown();
            try {
                taskPool.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("--- Worker Thread Pool đã dừng ---");
        }
    }

    private class TaskProcessor implements Runnable {
        private Socket clientSocket;

        public TaskProcessor(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            TaskData task = null;
            String errorMessage = null;

            try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {
                
                task = (TaskData) ois.readObject();
                System.out.println("Worker Thread #" + Thread.currentThread().getId() + " nhận Task: " + task.getRequestId());

                String inputPath = task.getVideoPath(); 
                
                File inputFile = new File(inputPath);
                if (!inputFile.exists()) {
                    errorMessage = "Lỗi: Không tìm thấy file video gốc tại " + inputPath;
                    throw new IOException(errorMessage);
                }

                String gifDir = ApplicationConfig.CONVERTED_DIR;
                String outputDir = WEB_APP_ROOT + gifDir;
                
                File dir = new File(outputDir);
                if (!dir.exists()) dir.mkdirs();
                
                String outputFileName = "gif_" + task.getRequestId() + ".gif";
                String outputPath = outputDir + File.separator + outputFileName;
                String relativeGifPath = gifDir + "/" + outputFileName;

                String duration = calculateDuration(task.getStartTime(), task.getEndTime());
                
                ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", 
                    "-i", inputPath,
                    "-ss", task.getStartTime(),
                    "-t", duration,
                    "-vf", "fps=10,scale=320:-1:flags=lanczos",
                    outputPath
                );
                
                pb.redirectErrorStream(true);
                Process process = pb.start();
                
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) { 
                        output.append(line).append("\n");
                    }
                }
                
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {
                    sendCompletionNotification(task.getRequestId(), "COMPLETED", relativeGifPath, null);
                } else {
                    errorMessage = "Lỗi FFmpeg (Exit Code: " + exitCode + ")\nLOG CHI TIẾT:\n" + output.toString();
                    
                    System.err.println(errorMessage); 
                    sendCompletionNotification(task.getRequestId(), "FAILED", null, errorMessage);
                }

            } catch (Exception e) {
                if (errorMessage == null) {
                    errorMessage = "Lỗi nội bộ Worker: " + e.getMessage();
                }
                System.err.println("Lỗi xử lý Task #" + (task != null ? task.getRequestId() : "N/A") + ": " + errorMessage);
                sendCompletionNotification(task != null ? task.getRequestId() : -1, "FAILED", null, errorMessage);
            } finally {
                try { 
                    if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
                } catch (IOException e) { /* ignore */ }
            }
        }
        
        private String calculateDuration(String start, String end) {
            String[] startParts = start.split(":");
            String[] endParts = end.split(":");
            
            long startSeconds = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
            long endSeconds = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);
            
            long duration = endSeconds - startSeconds;
            if (duration <= 0) duration = 1; 
            
            long hours = duration / 3600;
            long minutes = (duration % 3600) / 60;
            long seconds = duration % 60;
            
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }
    
    private void sendCompletionNotification(int requestId, String status, String gifPath, String errorMessage) {
        if (requestId == -1) return; 

        try (Socket socket = new Socket(SERVER_HOST, COMPLETION_PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            
            WorkerResponse response = new WorkerResponse(requestId, status, gifPath, errorMessage);
            oos.writeObject(response);
            oos.flush();
            System.out.println("--- Worker gửi phản hồi cho Request #" + requestId + ": " + status + " ---");

        } catch (IOException e) {
            System.err.println("Không thể gửi thông báo hoàn thành Task #" + requestId + " về Server: " + e.getMessage());
        }
    }
}