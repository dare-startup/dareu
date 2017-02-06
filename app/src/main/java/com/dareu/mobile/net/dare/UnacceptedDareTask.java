package com.dareu.mobile.net.dare;

import android.content.Context;

import com.dareu.mobile.activity.UnacceptedDaresActivity;
import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.UnacceptedDare;
import com.google.gson.Gson;

/**
 * Created by jose.rubalcaba on 02/03/2017.
 */

public class UnacceptedDareTask extends AbstractApacheTask {

    private AsyncTaskListener<UnacceptedDare> listener;

    public UnacceptedDareTask(Context cxt, AsyncTaskListener<UnacceptedDare> listener){
        super(cxt, "GET", null, SharedUtils.getProperty(PropertyName.UNACCEPTED_DARE, cxt), true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null){
            UnacceptedDare dare = new Gson().fromJson(result.getJsonResponse(), UnacceptedDare.class);
            listener.onTaskResponse(dare);
        }else listener.onError(getErrorMessage(result));
    }
}
