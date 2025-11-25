package model.Bean;

import java.io.Serializable;

public class TaskData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int requestId;
    private String videoPath;
    private String startTime;
    private String endTime;

    public TaskData(int requestId, String videoPath, String startTime, String endTime) {
        this.requestId = requestId;
        this.videoPath = videoPath;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getRequestId() { return requestId; }
    public String getVideoPath() { return videoPath; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

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