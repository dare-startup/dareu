package com.dareu.mobile.net.dare;

import android.content.Context;
import android.content.Entity;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.request.CreateDareRequest;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public class CreateDareTask extends AbstractApacheTask {

    private AsyncTaskListener<EntityRegistrationResponse> listener;

    public CreateDareTask(Context cxt, CreateDareRequest request, AsyncTaskListener<EntityRegistrationResponse> listener){
        super(cxt, "POST", request, SharedUtils.getProperty(PropertyName.CREATE_DARE, cxt), true);
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
