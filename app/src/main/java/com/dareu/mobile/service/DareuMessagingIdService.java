package com.dareu.mobile.service;

import android.content.Intent;
import android.util.Log;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public class DareuMessagingIdService extends FirebaseInstanceIdService {

    private static final String TAG = "DareuMessagingIdService";
    private AccountClientService accountService;

    public static final String MESSAGE_ACTION = "com.dareu.mobile.service.DareuMessagingIdService.ACTION";
    public static final String REG_ID = "com.dareu.mobile.service.DareuMessagingIdService.REG_ID";

    @Override
    public void onTokenRefresh() {
        if(accountService == null)
            RetroFactory
                    .getInstance()
                    .create(AccountClientService.class);
        String regId = FirebaseInstanceId.getInstance().getToken();
        //save reg id
        SharedUtils.setStringPreference(this, PrefName.GCM_TOKEN, regId);
    }
}
