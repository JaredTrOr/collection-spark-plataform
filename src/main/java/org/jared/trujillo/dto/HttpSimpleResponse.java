package org.jared.trujillo.dto;

public class HttpSimpleResponse {

    private final boolean success;
    private final Object data;
    private final String message;

    private HttpSimpleResponse(boolean success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() { return this.success; }
    public Object getData() { return this.data; }
    public String getMessage() { return this.message; }

    public static HttpSimpleResponse success(Object data) {
        return new HttpSimpleResponse(true, data, null);
    }

    public static HttpSimpleResponse success(Object data, String message) {
        return new HttpSimpleResponse(true, data, message);
    }

    public static HttpSimpleResponse success(String message) {
        return new HttpSimpleResponse(true, null, message);
    }

    public static HttpSimpleResponse error(String message) {
        return new HttpSimpleResponse(false, null, message);
    }
}
