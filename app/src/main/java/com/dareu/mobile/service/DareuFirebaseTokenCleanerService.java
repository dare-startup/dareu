package com.dareu.mobile.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

/**
 * Created by jose.rubalcaba on 03/15/2017.
 */

public class DareuFirebaseTokenCleanerService extends IntentService {

    public static final String ACTION  = "com.dareu.mobile.service.DareuFirebaseTokenCleanerService.ACTION";
    private static final String TAG = "FirebaseRemoverService";
    public DareuFirebaseTokenCleanerService() {
        super("DareuFirebaseTokenCleanerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try{
            FirebaseInstanceId.getInstance().deleteInstanceId();
            Log.i(TAG, "Deleted Firebase instance id");
        }catch(IOException ex){
            Log.e(TAG, ex.getMessage());
        }
    }
}
