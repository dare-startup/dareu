package com.dareu.mobile.net;

import android.content.Context;

import com.dareu.mobile.net.request.SignupRequest;
import com.dareu.mobile.utils.PropertyName;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public class SignupTask extends MultipartTask {
    public SignupTask(Context cxt, MultiparListener callback, SignupRequest request) {
        super(cxt, PropertyName.SIGNUP, callback);
        setRequestFields(request);
    }

    private void setRequestFields(SignupRequest request) {
        addFormField("name", request.getName());
        addFormField("email", request.getEmail());
        addFormField("username", request.getUsername());
        addFormField("password", request.getPassword());
        addFormField("birthday", request.getBirthday());
        addBitmapField("file", request.getBitmap());
    }
}
