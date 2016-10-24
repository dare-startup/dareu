package com.dareu.mobile.net;

import android.content.Context;

import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;

import java.util.Map;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public class FindFriendsTask extends AbstractTask{

    private AsyncTaskListener listener;

    public FindFriendsTask(Context cxt, String query, AsyncTaskListener listener) {
        super(SharedUtils.getProperty(PropertyName.FIND_FRIENDS_BY_QUERY, cxt) + query, "GET", cxt, true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        int statusCode = Integer.parseInt(result.get(STATUS_CODE));
        String json = result.get(JSON_RESPONSE);
        listener.onStatusCode(json, statusCode);
    }
}
