package com.dareu.mobile.net.account;

import android.content.Context;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 02/01/2017.
 */

public class ConfirmConnectionTask extends AbstractApacheTask {

    private AsyncTaskListener<EntityRegistrationResponse> listener;


    public ConfirmConnectionTask(Context cxt, String userId, boolean accepted, AsyncTaskListener<EntityRegistrationResponse> listener){
        super(cxt, "POST", null,
                String.format(SharedUtils.getProperty(PropertyName.CONFIRM_CONNECTION, cxt), userId, String.valueOf(accepted)), true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null){
            EntityRegistrationResponse response = new Gson().fromJson(result.getJsonResponse(), EntityRegistrationResponse.class);
            listener.onTaskResponse(response);
        }else{
            listener.onError(getErrorMessage(result));
        }
    }
}
