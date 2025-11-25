package model.Bean;

import java.io.Serializable;

public class WorkerResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int requestId;
    private String status; 
    private String gifPath; 
    private String errorMessage;

    public WorkerResponse(int requestId, String status, String gifPath, String errorMessage) {
        this.requestId = requestId;
        this.status = status;
        this.gifPath = gifPath;
        this.errorMessage = errorMessage;
    }

    // Getters
    public int getRequestId() { return requestId; }
    public String getStatus() { return status; }
    public String getGifPath() { return gifPath; }
    public String getErrorMessage() { return errorMessage; }
}