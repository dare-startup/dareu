package com.dareu.mobile.net.response;

/**
 * Created by jose.rubalcaba on 01/27/2017.
 */

public class ApacheResponseWrapper {
    private int statusCode;
    private String url;
    private String jsonResponse;

    public ApacheResponseWrapper() {
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

    public String getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }
}
