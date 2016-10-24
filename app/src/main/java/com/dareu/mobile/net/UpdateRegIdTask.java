package com.dareu.mobile.net;

import android.content.Context;

import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;

import java.util.Map;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public class UpdateRegIdTask extends AbstractTask {

    private AsyncTaskListener listener;

    public UpdateRegIdTask(Context cxt, String regId, AsyncTaskListener listener) {
        super(SharedUtils.getProperty(PropertyName.UPDATE_GCM_RE_ID, cxt) + regId, "POST", cxt, true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        //get status code
        int statusCode = Integer.parseInt(result.get(STATUS_CODE));
        String json = result.get(JSON_RESPONSE);

        listener.onStatusCode(json, statusCode);
    }
}
