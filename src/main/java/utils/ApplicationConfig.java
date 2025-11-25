package utils;

import java.io.File;

public class ApplicationConfig {
    
    public static final String DB_URL = "jdbc:mysql://localhost:3306/dut-video2gif";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "123456";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    public static final String WORKER_HOST = "localhost";
    public static final int TASK_SENDER_PORT = 9999;
    public static final int COMPLETION_LISTENER_PORT = 9998;


    public static final String BASE_DATA_PATH = "C:\\Users\\Admin\\Video2GIF_Data";
    public static final String UPLOAD_DIR = "uploaded_videos";
    public static final String CONVERTED_DIR = "converted_gifs";

    public static String getUploadDirAbsolutePath() {
        return BASE_DATA_PATH + File.separator + UPLOAD_DIR;
    }

    public static String getConvertedDirAbsolutePath() {
        return BASE_DATA_PATH + File.separator + CONVERTED_DIR;
    }
    
    public static void initializeStorage() {
        File uploadDir = new File(getUploadDirAbsolutePath());
        File convertedDir = new File(getConvertedDirAbsolutePath());
        
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        if (!convertedDir.exists()) {
            convertedDir.mkdirs();
        }
    }
}