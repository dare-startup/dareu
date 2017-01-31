package com.dareu.mobile.net;

import android.content.Context;

import com.dareu.web.dto.request.SigninRequest;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.AuthenticationResponse;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public class SigninTask extends AbstractApacheTask{

    private AsyncTaskListener<AuthenticationResponse> listener;

    public SigninTask(Context cxt, SigninRequest request, AsyncTaskListener listener) {
        super(cxt, "POST", request,  SharedUtils.getProperty(PropertyName.SIGNIN, cxt), false);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {

        AuthenticationResponse response = new AuthenticationResponse();
        switch(result.getStatusCode()){
            case 200:
                response = new Gson().fromJson(result.getJsonResponse(), AuthenticationResponse.class);
                listener.onTaskResponse(response);
                break;
            case 500:
                //set messages
                response.setMessage("An error has occurred on the server processing the request, please try again");
                response.setToken(null);
                response.setDate(null);
                break;
            case 400:

                break;
            case 401:
                //not authenticated
                response = new Gson().fromJson(result.getJsonResponse(), AuthenticationResponse.class);
                listener.onTaskResponse(response);
                break;
        }
    }
}
