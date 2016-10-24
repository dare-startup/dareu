package com.dareu.mobile.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by jose.rubalcaba on 10/23/2016.
 */

public abstract class MultipartTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "MULTIPARTCLIENTTASK";

    private String resourceUrl;
    private HttpURLConnection connection;
    private Context context;
    private MultipartEntity entity;
    private OutputStream out;
    private String boundary;
    private boolean connected;
    private MultiparListener callback;

    public MultipartTask(Context cxt, PropertyName resourcePath, MultiparListener callback){
        this.context = cxt;
        this.resourceUrl = SharedUtils.getProperty(PropertyName.DEBUG_SERVER, cxt) +
                SharedUtils.getProperty(resourcePath, cxt);

        this.callback = callback;
        this.entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, boundary, Charset.forName("UTF-8"));
    }

    private void init(){
        //configure connection
        try{
            boundary = String.valueOf(System.currentTimeMillis());
            URL url = new URL(resourceUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Length", entity.getContentLength() + "");
            connection.setRequestProperty(entity.getContentType().getName(),
                    entity.getContentType().getValue());
            out = connection.getOutputStream();
            entity.writeTo(out);
            connection.connect();
            connected = true;
        }catch(Exception ex){
            Log.e(TAG, "Exception: " + ex.getMessage());
            connected = false;
            if(callback != null)
                callback.onError("Could not configure connection");
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        init();
        if(connected){
            int statusCode;
            try{
                statusCode = connection.getResponseCode();
                //read response
                String jsonResponse = readConnectionResponse();
                //callback
                if(callback != null){
                    callback.onResponse(statusCode, jsonResponse);
                }
            }catch(Exception ex){
                Log.e(TAG, "Error: " + ex.getMessage());
                if(callback != null)
                    callback.onError("Could not get response from server");
            }
        }else{
            Log.e(TAG, "Not connected, will not try to post request");
            if(callback != null)
                callback.onError("Could not connect to server");
        }
        return null;
    }

    private String readConnectionResponse() {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        if(connected){
            try{
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = null;
                while((line = reader.readLine()) != null)
                    builder.append(line);
            }catch(Exception ex){
                Log.e(TAG, "Error reading response: " + ex.getMessage());
                if(callback != null)
                    callback.onError("Could not read response");
            }
        }
        return builder.toString();
    }

    public void addFormField(String fieldName, String fieldValue){
        if(entity != null){
            entity.addPart(fieldName, new StringBody(fieldValue, ContentType.TEXT_PLAIN));
            Log.i(TAG, "Added multipart " + fieldName);
        }else{
            Log.i(TAG, "Entity is null, could not add form field");
            if(callback != null)
                callback.onError("Could not add form field");
        }
    }

    public void addBitmapField(String fieldName, Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 9, bos);
        byte[] bytes = bos.toByteArray();
        ContentBody body = new ByteArrayBody(bytes, ContentType.APPLICATION_OCTET_STREAM, fieldName);

        if(entity != null){
            entity.addPart(fieldName, body);
            Log.i(TAG, "Added multipart " + fieldName);
        }else {
            Log.i(TAG, "Entity is null, could not add form field");
            if(callback != null)
                callback.onError("Could not add bitmap");
        }
    }
}
