package com.dareu.mobile.net.multipart;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public interface MultiparListener {
    public void onResponse(int statusCode, String jsonResponse);
    public void onError(String message);
}
