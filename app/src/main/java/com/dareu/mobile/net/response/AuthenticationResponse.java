package com.dareu.mobile.net.response;

/**
 * Created by jose.rubalcaba on 10/11/2016.
 */

public class AuthenticationResponse {
    private String token;
    private String date;
    private String message;

    public AuthenticationResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
