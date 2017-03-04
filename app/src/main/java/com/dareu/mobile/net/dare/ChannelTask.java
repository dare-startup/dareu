package com.dareu.mobile.net.dare;

import android.content.Context;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.DareResponseDescription;
import com.dareu.web.dto.response.entity.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by jose.rubalcaba on 03/03/2017.
 */

public class ChannelTask extends AbstractApacheTask {

    private AsyncTaskListener<Page<DareResponseDescription>> listener;

    public ChannelTask(Context context, int pageNumber, AsyncTaskListener<Page<DareResponseDescription>> listener) {
        super(context, "GET", null, String.format(
                String.format(SharedUtils.getProperty(PropertyName.CHANNEL, context), pageNumber), pageNumber), true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result == null)
            listener.onError("No response from server :(");
        switch(result.getStatusCode()){
            case 200:
                Type type = new TypeToken<Page<DareResponseDescription>>(){}.getType();
                Page<DareResponseDescription> page = new Gson().fromJson(result.getJsonResponse(), type);
                listener.onTaskResponse(page);
                break;
            case 500:
                listener.onError(getErrorMessage(result));
                break;
            case 404:
                listener.onError(getErrorMessage(result));
                break;
        }
    }
}
