package com.dareu.mobile.activity.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public class DareuMessagingService extends FirebaseMessagingService {

    private static final String TAG = "DareuMessagingService";
    public DareuMessagingService(){
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "Message received: " + remoteMessage.getMessageType() + ", " + remoteMessage.getData().toString());
    }


}
