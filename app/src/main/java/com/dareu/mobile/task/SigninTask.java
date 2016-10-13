package com.dareu.mobile.task;

import android.content.Context;

import com.dareu.mobile.task.request.SigninRequest;
import com.dareu.mobile.utils.PropertyName;

import java.util.Map;

/**
 * Created by jose.rubalcaba on 10/11/2016.
 */

public class SigninTask extends AbstractTask {

    private AsyncTaskListener listener;

    public SigninTask(Context cxt, SigninRequest request){
        super(PropertyName.SIGNIN, "POST", cxt, false, request);
    }

    public void setListener(AsyncTaskListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        //get json string
        String json = result.get(JSON_RESPONSE);
        int statusCode = Integer.parseInt(result.get(STATUS_CODE));
        if(statusCode == 200){
            if(listener != null)
                listener.onSuccess(json);
        }else listener.onStatusCode(json, statusCode);
    }
}
