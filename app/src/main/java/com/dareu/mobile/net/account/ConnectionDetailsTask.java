package com.dareu.mobile.net.account;

import android.content.Context;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.ConnectionDetails;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 02/02/2017.
 */

public class ConnectionDetailsTask extends AbstractApacheTask {

    private AsyncTaskListener<ConnectionDetails> listener;

    public ConnectionDetailsTask(Context cxt, String friendshipId, AsyncTaskListener<ConnectionDetails> listener){
        super(cxt, "GET", null, SharedUtils.getProperty(PropertyName.CONNECTION_DETAILS, cxt) + friendshipId, true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null){
            ConnectionDetails details = new Gson().fromJson(result.getJsonResponse(), ConnectionDetails.class);
            listener.onTaskResponse(details);
        }else{
            listener.onError(getErrorMessage(result));
        }
    }
}
