package com.dareu.mobile.task;

/**
 * Created by jose.rubalcaba on 10/08/2016.
 */

public interface AsyncTaskListener {

    static final String JSON_RESPONSE = "jsonResponse";
    static final String STATUS_CODE = "statusCode";

    public void onSuccess(String jsonText);
    public void onStatusCode(String jsonText, int statusCode);
}
