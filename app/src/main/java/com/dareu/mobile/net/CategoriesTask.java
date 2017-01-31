package com.dareu.mobile.net;

import android.content.Context;

import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.CategoryDescription;
import com.dareu.web.dto.response.entity.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by jose.rubalcaba on 01/30/2017.
 */

public class CategoriesTask extends AbstractApacheTask {

    private AsyncTaskListener<Page<CategoryDescription>> listener;

    public CategoriesTask(Context context, int pageNumber, AsyncTaskListener<Page<CategoryDescription>> listener) {
        super(context, "GET", null, SharedUtils.getProperty(PropertyName.CATEGORIES, context) + pageNumber, true);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null){
            Type type = new TypeToken<Page<CategoryDescription>>(){}.getType();
            Page<CategoryDescription> page = new Gson().fromJson(result.getJsonResponse(), type);
            listener.onTaskResponse(page);
        }else{
            listener.onError("Could not get categories, try again");
        }
    }
}
