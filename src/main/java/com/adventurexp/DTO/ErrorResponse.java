package com.adventurexp.DTO;

public class ErrorResponse {
    private String errorCode;
    private String message;
    private int statusCode;
    private String url;

    public ErrorResponse(String errorCode, String message, int statusCode, String url) {
        this.errorCode = errorCode;
        this.message = message;
        this.statusCode = statusCode;
        this.url = url;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}