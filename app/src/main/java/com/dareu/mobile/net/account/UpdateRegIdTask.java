package com.dareu.mobile.net.account;

import android.content.Context;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 01/31/2017.
 */

public class UpdateRegIdTask extends AbstractApacheTask {

    private AsyncTaskListener<UpdatedEntityResponse> listener;

    public UpdateRegIdTask(Context cxt, AsyncTaskListener<UpdatedEntityResponse> listener){
        super(cxt, "POST", null,
                SharedUtils.getProperty(PropertyName.UPDATE_GCM_RE_ID, cxt) + SharedUtils.getStringPreference(cxt, PrefName.GCM_TOKEN),
                true);
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
