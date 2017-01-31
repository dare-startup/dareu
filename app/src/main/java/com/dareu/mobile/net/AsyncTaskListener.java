package com.dareu.mobile.net;

import com.dareu.mobile.net.response.ApacheResponseWrapper;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public interface AsyncTaskListener<T> {
    public void onTaskResponse(T response);
    public void onError(String errorMessage);
}
