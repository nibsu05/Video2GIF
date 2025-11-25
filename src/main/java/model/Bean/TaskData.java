package model.Bean;

import java.io.Serializable;

// Cần Serializable để có thể gửi qua Socket
public class TaskData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Dữ liệu Task được gửi từ Server đến Worker
    private int requestId;
    private String videoPath; // Đường dẫn vật lý đến file video
    private String startTime;
    private String endTime;

    // Constructor
    public TaskData(int requestId, String videoPath, String startTime, String endTime) {
        this.requestId = requestId;
        this.videoPath = videoPath;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public int getRequestId() { return requestId; }
    public String getVideoPath() { return videoPath; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    // Setters (Nếu cần)
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    @Override
    public String toString() {
        return "TaskData [requestId=" + requestId + ", videoPath=" + videoPath + ", startTime=" + startTime
                + ", endTime=" + endTime + "]";
    }
}