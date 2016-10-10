package com.dareu.mobile.activity.service;

import android.util.Log;

import com.dareu.mobile.task.GcmTask;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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
        if(SharedUtils.getStringPreference(getApplicationContext(), PrefName.SIGNIN_TOKEN) != null){
            boolean refreshed = SharedUtils.updateGcmTask(getApplicationContext(), regId);
            if(refreshed){
                //initialize firebase application
            }
        }
    }
}
