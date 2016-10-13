package com.dareu.mobile.task;

import android.content.Context;

import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;

import java.util.Map;

/**
 * Created by jose.rubalcaba on 10/12/2016.
 */

public class CategoriesTask extends AbstractTask {

    private AsyncTaskListener listener;

    public CategoriesTask(Context cxt, AsyncTaskListener listener) {
        super(PropertyName.CATEGORIES, "GET", cxt, true, null);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        //get json response
        String jsonResponse = result.get(JSON_RESPONSE);
        int statusCode = Integer.parseInt(result.get(STATUS_CODE));

        if(statusCode != 200)
            listener.onStatusCode(jsonResponse, statusCode);
        else listener.onSuccess(jsonResponse);

    }
}
