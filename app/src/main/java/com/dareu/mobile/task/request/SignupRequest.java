package com.dareu.mobile.task.request;

import android.net.Uri;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public class SignupRequest extends MultipartRequest{
    private String name;
    private String email;
    private String username;
    private String password;
    private String regId;
    private String birthday;

    public SignupRequest() {
        super(null);
    }

    public SignupRequest(String name, String email, String username, String password, String regId, Uri image, String birthday) {
        super(image);
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.regId = regId;
        this.birthday = birthday;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
