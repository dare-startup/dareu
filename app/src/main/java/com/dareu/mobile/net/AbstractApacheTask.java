package com.dareu.mobile.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dareu.mobile.net.handler.ApacheResponseHandler;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.google.gson.Gson;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by jose.rubalcaba on 01/27/2017.
 */

public abstract class AbstractApacheTask extends AsyncTask<Void, Void, ApacheResponseWrapper> {

    private String method;
    private Object entity;
    private String url;
    private boolean authenticated;
    private DefaultHttpClient client;
    private String token;

    private static final String POST = "POST";
    private static final String GET = "GET";

    public AbstractApacheTask(Context context, String method, Object entity, String contextPath, boolean authenticated){
        this.method = method;
        this.entity = entity;
        this.token = SharedUtils.getStringPreference(context, PrefName.SIGNIN_TOKEN);
        this.url = SharedUtils.getProperty(PropertyName.DEBUG_SERVER, context) + contextPath;
        this.authenticated = authenticated;
        this.client = new DefaultHttpClient();

    }


    @Override
    protected ApacheResponseWrapper doInBackground(Void... params) {
        if(method.equalsIgnoreCase(POST)){
            HttpPost post = new HttpPost(url);
            post.addHeader("Accept", "application/json");
            if(authenticated)
                post.addHeader("Authorization", this.token);

            try{
                post.setEntity(new StringEntity(new Gson().toJson(entity), "UTF-8"));
                post.addHeader("Content-Type", "application/json");

                //send request
                return client.execute(post, new ApacheResponseHandler(url));
            }catch(UnsupportedEncodingException ex){
                Log.e("AbstractApacheTask", ex.getMessage());
                return null;
            }catch(IOException ex){
                Log.e("AbstractApacheTask", ex.getMessage());
                return null;
            }
        }else {
            HttpGet get = new HttpGet(url);
            get.addHeader("Accept", "application/json");
            if(authenticated)
                get.addHeader("Authorization", this.token);
            try{
                return client.execute(get, new ApacheResponseHandler(url));
            }catch(IOException ex){
                Log.e("AbstractApacheTask", ex.getMessage());
                return null;
            }
        }
    }



    @Override
    protected abstract void onPostExecute(ApacheResponseWrapper result);

    protected String getErrorMessage(ApacheResponseWrapper wrapper){
        if(wrapper == null)
            return "No response received from server, try again";
        switch(wrapper.getStatusCode()){
            case 200:
                return "Success";
            case 404:
                return "Server temporarily out of business, try again later";
            case 500:
                return "Something bad has happened, try again";
            case 415:
                return "Someone zap out this shitty developer";
            case 401:
                return "You are not authorized to view this content";
            default:
                return "N/A";
        }
    }
}
