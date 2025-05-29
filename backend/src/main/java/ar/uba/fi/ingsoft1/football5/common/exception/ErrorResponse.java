package ar.uba.fi.ingsoft1.football5.common.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String error;
    private Object message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, Object message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public Object getMessage() { return message; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setStatus(int status) { this.status = status; }
    public void setError(String error) { this.error = error; }
    public void setMessage(Object message) { this.message = message; }
    public void setPath(String path) { this.path = path; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

