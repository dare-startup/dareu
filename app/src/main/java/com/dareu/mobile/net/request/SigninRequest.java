package com.dareu.mobile.net.request;

/**
 * Created by jose.rubalcaba on 10/11/2016.
 */

public class SigninRequest {

    private String user;
    private String password;
    public SigninRequest(String username, String password) {
        this.user = username;
        this.password = password;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
