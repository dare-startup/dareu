package com.dareu.mobile.net.dare;

import android.content.Context;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 02/17/2017.
 */

public class DareExpirationTask extends AbstractApacheTask {

    private AsyncTaskListener<UpdatedEntityResponse> listener;
    public DareExpirationTask(Context context, String dareId, AsyncTaskListener<UpdatedEntityResponse> listener) {
        super(context, "POST", null, String.format(SharedUtils.getProperty(PropertyName.DARE_EXPIRATION, context), dareId), true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null){
            UpdatedEntityResponse response = new Gson().fromJson(result.getJsonResponse(), UpdatedEntityResponse.class);
            listener.onTaskResponse(response);

        }else listener.onError(getErrorMessage(result));
    }
}
