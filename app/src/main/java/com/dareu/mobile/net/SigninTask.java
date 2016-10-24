package com.dareu.mobile.net;

import android.content.Context;

import com.dareu.mobile.net.request.SigninRequest;
import com.dareu.mobile.utils.PropertyName;

import java.util.Map;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public class SigninTask extends AbstractTask {

    private AsyncTaskListener listener;

    public SigninTask(Context cxt, SigninRequest request, AsyncTaskListener listener) {
        super(PropertyName.SIGNIN, "POST", cxt, false, request);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        //get status code
        int statusCode = Integer.parseInt(result.get(STATUS_CODE));
        String json = result.get(JSON_RESPONSE);

        listener.onStatusCode(json, statusCode);
    }
}
