package com.dareu.mobile.net.account;

import android.content.Context;
import android.os.AsyncTask;

import com.dareu.mobile.net.AbstractApacheTask;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.handler.ApacheResponseHandler;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.google.gson.Gson;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jose.rubalcaba on 02/01/2017.
 */

public class UpdateProfileImageTask extends AsyncTask<Void, Void, ApacheResponseWrapper> {

    private HttpClient client = new DefaultHttpClient();
    private Context cxt;

    private InputStream file;
    private AsyncTaskListener<UpdatedEntityResponse> listener;

    public UpdateProfileImageTask(Context cxt, InputStream file, AsyncTaskListener<UpdatedEntityResponse> listener){
        this.cxt = cxt;
        this.file = file;
        this.listener = listener;
    }

    @Override
    protected ApacheResponseWrapper doInBackground(Void... params) {
        String host = SharedUtils.getProperty(PropertyName.DEBUG_SERVER, cxt) +
                SharedUtils.getProperty(PropertyName.UPDATE_IMAGE_PROFILE, cxt);
        HttpPost post = new HttpPost(host);
        post.addHeader("Accept", "application/json");
        post.addHeader("Authorization", SharedUtils.getStringPreference(cxt, PrefName.SIGNIN_TOKEN));
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentBody body = new InputStreamBody(file, "image/jpeg");
        entity.addPart("image", body);
        post.setEntity(entity);

        try{
            return client.execute(post, new ApacheResponseHandler(host));
        }catch(IOException ex){
            return null;
        }
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper result) {
        if(result != null){
            UpdatedEntityResponse response = new Gson().fromJson(result.getJsonResponse(), UpdatedEntityResponse.class);
            listener.onTaskResponse(response);
        }else{
            listener.onError(SharedUtils.getErrorMessage(result));
        }
    }
}
