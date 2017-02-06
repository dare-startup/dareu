package com.dareu.mobile.activity.service;

import android.util.Log;

import com.dareu.mobile.net.handler.ApacheResponseHandler;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public class DareuMessagingIdService extends FirebaseInstanceIdService {

    private static final String TAG = "DareuMessagingIdService";
    @Override
    public void onTokenRefresh() {
        String regId = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Token refreshed: " + regId);
        //refresh token
        String signinToken = SharedUtils.getStringPreference(getApplicationContext(), PrefName.SIGNIN_TOKEN);
        if(signinToken != null && ! signinToken.isEmpty()){
            boolean refreshed = SharedUtils.updateGcmTask(getApplicationContext(), regId);
            if(refreshed){
                //creates a request to update the reg id on server
                try{
                    executeRequest(regId);
                }catch(IOException ex){
                    Log.e(TAG, "Could not update regId: " + ex.getMessage());
                }
            }
        }else{
            //just save the reg id and check on main activity if it was already saved
            SharedUtils.setStringPreference(getApplicationContext(), PrefName.GCM_TOKEN, regId);
            SharedUtils.setStringPreference(getApplicationContext(), PrefName.ALREADY_REGISTERED_GCM_TOKEN, Boolean.FALSE.toString());
        }
    }

    private void executeRequest(String regId)throws IOException {
        HttpClient client = new DefaultHttpClient();
        String url = SharedUtils.getProperty(PropertyName.UPDATE_GCM_RE_ID, getApplicationContext()) + regId;
        String token = SharedUtils.getStringPreference(getApplicationContext(), PrefName.SIGNIN_TOKEN);
        HttpPost post = new HttpPost(url);
        post.addHeader("Accept", "application/json");
        post.addHeader("Authorization", token);

        ApacheResponseWrapper wrapper = client.execute(post, new ApacheResponseHandler(url));
        if(wrapper != null){
            UpdatedEntityResponse response = new Gson().fromJson(wrapper.getJsonResponse(), UpdatedEntityResponse.class);
            if(response.isSuccess()){
                Log.i(TAG, response.getMessage());
            }else Log.i(TAG, response.getMessage());
        }
    }
}
