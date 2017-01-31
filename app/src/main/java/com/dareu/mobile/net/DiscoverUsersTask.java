package com.dareu.mobile.net;

import android.content.Context;

import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.DiscoverUserAccount;
import com.dareu.web.dto.response.entity.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by jose.rubalcaba on 01/30/2017.
 */

public class DiscoverUsersTask extends AbstractApacheTask {

    private AsyncTaskListener<Page<DiscoverUserAccount>> listener;

    public DiscoverUsersTask(Context cxt, AsyncTaskListener<Page<DiscoverUserAccount>> listener, int pageNumber){
        super(cxt, "GET", null, SharedUtils.getProperty(PropertyName.DISCOVER_USERS, cxt) + pageNumber, true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        Page<DiscoverUserAccount> page;
        if(result != null){
            Type type = new TypeToken<Page<DiscoverUserAccount>>(){}.getType();
            page = new Gson().fromJson(result.getJsonResponse(), type);
            listener.onTaskResponse(page);
        }else
            listener.onError("Could not fetch users, try again");

    }
}
