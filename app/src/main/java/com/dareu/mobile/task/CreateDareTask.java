package com.dareu.mobile.task;

import android.content.Context;

import com.dareu.mobile.task.request.NewDareRequest;
import com.dareu.mobile.utils.PropertyName;

import java.util.Map;

/**
 * Created by jose.rubalcaba on 10/14/2016.
 */

public class CreateDareTask extends AbstractTask{

    private AsyncTaskListener listener;

    public CreateDareTask(Context cxt, NewDareRequest request, AsyncTaskListener listener) {
        super(PropertyName.CREATE_DARE, "POST", cxt, true, request);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        String json = result.get(JSON_RESPONSE);
        Integer statusCode = Integer.parseInt(result.get(STATUS_CODE));
        if(statusCode == 200)
            listener.onSuccess(json);
        else
            listener.onStatusCode(json, statusCode);
    }
}
