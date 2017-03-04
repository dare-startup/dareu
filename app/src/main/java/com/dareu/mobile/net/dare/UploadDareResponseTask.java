package com.dareu.mobile.net.dare;

import android.content.Context;
import android.os.AsyncTask;

import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.handler.ApacheResponseHandler;
import com.dareu.mobile.net.request.UploadDareResponseRequest;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.google.gson.Gson;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by jose.rubalcaba on 02/16/2017.
 */

public class UploadDareResponseTask extends AsyncTask<Void, Void, ApacheResponseWrapper> {

    private HttpClient client = new DefaultHttpClient();

    private AsyncTaskListener<EntityRegistrationResponse> listener;
    private UploadDareResponseRequest request;
    private Context cxt;

    public UploadDareResponseTask(Context cxt, UploadDareResponseRequest request, AsyncTaskListener<EntityRegistrationResponse> listener){
        this.cxt = cxt;
        this.request = request;
        this.listener = listener;
    }

    @Override
    protected ApacheResponseWrapper doInBackground(Void... params) {
        String host = SharedUtils.getProperty(PropertyName.DEBUG_SERVER, cxt);
        String path = SharedUtils.getProperty(PropertyName.UPLOAD_DARE_RESPONSE, cxt);

        String token = SharedUtils.getStringPreference(cxt, PrefName.SIGNIN_TOKEN);
        HttpPost post = new HttpPost(host + path);
        post.addHeader("Authorization", token);
        post.addHeader("Accept", "application/json");
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentBody videoPart = new InputStreamBody(request.getVideo(), "video/mp4");
        entity.addPart("file", videoPart);
        ContentBody thumbPart = new InputStreamBody(request.getThumbImage(), "image/jpeg");
        entity.addPart("thumb", thumbPart);
        ContentBody dareIdPart = new StringBody(request.getDareid(), ContentType.TEXT_PLAIN);
        entity.addPart("dareId", dareIdPart);
        ContentBody commentPart = new StringBody(request.getComment(), ContentType.TEXT_PLAIN);
        entity.addPart("comment", commentPart);

        post.setEntity(entity);

        try{
            return client.execute(post, new ApacheResponseHandler(path));
        }catch(IOException ex){
            return null;
        }
    }

    @Override
    protected void onPostExecute(ApacheResponseWrapper apacheResponseWrapper) {
        if(apacheResponseWrapper != null){
            EntityRegistrationResponse response = new Gson().fromJson(apacheResponseWrapper.getJsonResponse(), EntityRegistrationResponse.class);
            listener.onTaskResponse(response);
        }else listener.onError(apacheResponseWrapper.getJsonResponse());
    }
}
