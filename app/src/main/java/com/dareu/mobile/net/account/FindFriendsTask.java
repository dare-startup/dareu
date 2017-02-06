package com.dareu.mobile.net.account;

import android.content.Context;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.FriendSearchDescription;
import com.dareu.web.dto.response.entity.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public class FindFriendsTask extends AbstractApacheTask {

    private AsyncTaskListener<Page<FriendSearchDescription>> listener;

    public FindFriendsTask(Context cxt, String query, AsyncTaskListener<Page<FriendSearchDescription>> listener, int pageNumber) {
        super(cxt, "GET", null,
                String.format(SharedUtils.getProperty(PropertyName.FIND_FRIENDS_BY_QUERY, cxt),
                        pageNumber, query), true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null){
            Type type = new TypeToken<Page<FriendSearchDescription>>(){}.getType();
            Page<FriendSearchDescription> page = new Gson().fromJson(result.getJsonResponse(), type);
            listener.onTaskResponse(page);
        }else{
            listener.onError(getErrorMessage(result));
        }
    }
}
