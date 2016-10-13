package com.dareu.mobile.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.MultipartEntity;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.dareu.mobile.task.request.SignupRequest;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public class SignupTask extends AsyncTask<Void, Void, Void>{

    private static final String TAG = "SignupTask";
    private AsyncTaskListener listener;
    private Context cxt;
    private String url;
    private SignupRequest request;

    private HttpClient client = new DefaultHttpClient();

    public SignupTask(Context cxt, AsyncTaskListener listener, SignupRequest request){
        this.url = SharedUtils.getProperty(PropertyName.DEBUG_SERVER, cxt) + SharedUtils.getProperty(PropertyName.SIGNUP, cxt);
        this.cxt = cxt;
        this.listener = listener;
        this.request = request;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpPost post = new HttpPost(url);

        Part[] parts = new Part[7];
        //populate
        parts[0] = new StringPart("name", request.getName());
        parts[1] = new StringPart("email", request.getEmail());
        parts[2] = new StringPart("username", request.getUsername());
        parts[3] = new StringPart("password", request.getPassword());
        parts[5] = new StringPart("birthday", request.getBirthday());
        try{
            parts[6] = new FilePart("file", new File(request.getFile().getPath()), "application/octet-stream", ""); //TODO: finish this
        }catch(FileNotFoundException ex){
            Log.e(TAG, "Could not found image: " + ex.getMessage());
            return null;
        }
        post.setEntity(new MultipartEntity(parts));

        try{
            HttpResponse response = client.execute(post);
            if(listener != null){
                if(response.getStatusLine().getStatusCode() == 200)
                    listener.onSuccess(EntityUtils.toString(response.getEntity()));
                else listener.onStatusCode(EntityUtils.toString(response.getEntity()), response.getStatusLine().getStatusCode());
            }
        }catch(IOException ex){
            Log.e(TAG, "Could not complete request: " + ex.getMessage());
            return null;
        }
        return null;
    }
}
