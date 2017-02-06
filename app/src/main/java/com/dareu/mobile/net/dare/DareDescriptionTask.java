package com.dareu.mobile.net.dare;

import android.content.Context;
import android.util.Log;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.DareDescription;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 02/01/2017.
 */

public class DareDescriptionTask extends AbstractApacheTask {

    private static final String TAG = DareDescriptionTask.class.getName();
    private AsyncTaskListener<DareDescription> listener;

    public DareDescriptionTask(Context cxt, AsyncTaskListener<DareDescription> listener, String dareId){
        super(cxt, "GET", null, SharedUtils.getProperty(PropertyName.FIND_DARE_DESCRIPTION, cxt) + dareId, true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null){
            if(result.getStatusCode() == 200){
                DareDescription desc = new Gson().fromJson(result.getJsonResponse(), DareDescription.class);
                listener.onTaskResponse(desc);
            }else {
                Log.i(TAG, result.getJsonResponse());
                listener.onError(getErrorMessage(result));
            }
        }else{
            listener.onError(getErrorMessage(result));
        }
    }
}
