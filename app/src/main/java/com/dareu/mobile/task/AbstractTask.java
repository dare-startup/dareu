package com.dareu.mobile.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public abstract class AbstractTask extends AsyncTask<Void, Void, Map<String, String>> {

    private static final String TAG = "AbstractAsyncTask";

    protected static final String JSON_RESPONSE = "jsonResponse";
    protected static final String STATUS_CODE = "statusCode";

    protected Context cxt;
    private HttpURLConnection conn;
    private String url;
    private String requestType = "GET"; //by default
    private boolean authenticated;
    protected Object entity;
    private int statusCode;

    public AbstractTask(String url, Context cxt, boolean authenticated){
        this.url = url;
        this.cxt = cxt;
        this.authenticated = authenticated;
    }

    public AbstractTask(String url, String requestType, Context cxt, boolean authenticated){
        this.url = url;
        this.cxt = cxt;
        this.requestType = requestType;
        this.authenticated = authenticated;
    }

    public AbstractTask(PropertyName propertyName, String requestType, Context cxt, boolean authenticated, Object entity){
        //append url to host
        this.url = SharedUtils.getProperty(PropertyName.DEBUG_SERVER, cxt) +
                SharedUtils.getProperty(propertyName, cxt);
        this.cxt = cxt;
        this.requestType = requestType;
        this.authenticated = authenticated;
        this.entity = entity;
    }

    /**
     * Execute a request to a url
     * @param params
     * @return
     */
    @Override
    protected Map<String, String> doInBackground(Void... params) {
        URL u = null;
        try{
            u = new URL(url);
            conn = (HttpURLConnection)u.openConnection();
            conn.setRequestMethod(requestType);
            if(requestType.equalsIgnoreCase("POST")){
                if (entity != null) {
                    conn.setDoOutput(true);
                    OutputStream out = conn.getOutputStream();
                    out.write(new Gson().toJson(entity).getBytes());
                }
            }
            conn.setDoInput(true);
            conn.setRequestProperty("Accept", "application/json");

            if(authenticated){
                String authenticationToken = SharedUtils.getStringPreference(cxt, PrefName.SIGNIN_TOKEN);
                conn.setRequestProperty("Authentication", authenticationToken);
            }

            statusCode = conn.getResponseCode();
            //get input stream
            InputStream in = conn.getInputStream();
            //read
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null)
                builder.append(line);

            String responseBody = builder.toString();

            Map<String, String> responseMap = new HashMap<String, String>();
            responseMap.put(JSON_RESPONSE, responseBody);
            responseMap.put(STATUS_CODE, String.valueOf(statusCode));

            return responseMap;
        }catch (MalformedURLException ex){
            Log.e(TAG, "Malformed URL: " + ex.getMessage());
            return null;
        }catch(IOException ex){
            Log.e(TAG, "Could not send/receive: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Post execution
     * @param result
     */
    @Override
    protected abstract void onPostExecute(Map<String, String> result);
}
