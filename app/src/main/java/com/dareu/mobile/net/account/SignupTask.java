package com.dareu.mobile.net.account;

import android.content.Context;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.web.dto.request.SignupRequest;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.AuthenticationResponse;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 01/29/2017.
 */

public class SignupTask extends AbstractApacheTask {
    private AsyncTaskListener<AuthenticationResponse> listener;

    public SignupTask(Context cxt, SignupRequest request, AsyncTaskListener<AuthenticationResponse> listener){
        super(cxt, "POST", request, SharedUtils.getProperty(PropertyName.SIGNUP, cxt), false);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        AuthenticationResponse response;
        if(result != null){
            response = new Gson().fromJson(result.getJsonResponse(), AuthenticationResponse.class);

            listener.onTaskResponse(response);
        }else{
            listener.onError(getErrorMessage(result));
        }
    }
}
