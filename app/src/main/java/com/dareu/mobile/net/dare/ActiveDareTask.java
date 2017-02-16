package com.dareu.mobile.net.dare;

import android.content.Context;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.ActiveDare;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 02/15/2017.
 */

public class ActiveDareTask extends AbstractApacheTask {

    private AsyncTaskListener<ActiveDare> listener;

    public ActiveDareTask(Context context, AsyncTaskListener<ActiveDare> listener) {
        super(context, "GET", null, SharedUtils.getProperty(PropertyName.ACTIVE_DARE, context), true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null && ! result.getJsonResponse().isEmpty()){
            ActiveDare dare = new Gson().fromJson(result.getJsonResponse(), ActiveDare.class);
            listener.onTaskResponse(dare);
        }else listener.onError(getErrorMessage(result));
    }
}
