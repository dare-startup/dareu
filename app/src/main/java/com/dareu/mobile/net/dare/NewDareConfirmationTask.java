package com.dareu.mobile.net.dare;

import android.content.Context;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.request.DareConfirmationRequest;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 02/01/2017.
 */

public class NewDareConfirmationTask extends AbstractApacheTask {

    private AsyncTaskListener<UpdatedEntityResponse> listener;

    public NewDareConfirmationTask(Context cxt, DareConfirmationRequest request, AsyncTaskListener<UpdatedEntityResponse> listener){
        super(cxt, "POST", request, SharedUtils.getProperty(PropertyName.DARE_CONFIRMATION, cxt), true);
        this.listener = listener;
    }
    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null){
            UpdatedEntityResponse response = new Gson().fromJson(result.getJsonResponse(), UpdatedEntityResponse.class);
            listener.onTaskResponse(response);
        }else{
            listener.onError(getErrorMessage(result));
        }
    }
}
