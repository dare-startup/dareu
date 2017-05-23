package com.dareu.mobile.activity.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dareu.mobile.R;

/**
 * Created by jose.rubalcaba on 03/25/2017.
 */

public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        PreferenceType type = PreferenceType.fromKey(key);
        switch(type){
            case CONTACTED_NOTIFICATION:
                break;
            case DARED_NOTIFICATION:
                break;
            case DELETE_ACCOUNT:
                break;
            case EMAIL_ACCOUNT:
                break;
            case PASSWORD:
                break;
            case UPDATE_NOTIFICATION:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    enum PreferenceType{
        EMAIL_ACCOUNT("changeEmailPreference"),
        PASSWORD("changePasswordPreference"),
        DARED_NOTIFICATION("daredNotificationPreference"),
        CONTACTED_NOTIFICATION("contactNotificationPreference"),
        UPDATE_NOTIFICATION("updateNotificationPreference"),
        DELETE_ACCOUNT("deleteAccountPreference");

        String value;
        PreferenceType(String value){
            this.value = value;
        }

        public static PreferenceType fromKey(String key){
            if(key.equals(EMAIL_ACCOUNT.value))
                return EMAIL_ACCOUNT;
            else if(key.equals(PASSWORD.value))
                return PASSWORD;
            else if(key.equals(DARED_NOTIFICATION.value))
                return DARED_NOTIFICATION;
            else if(key.equals(CONTACTED_NOTIFICATION.value))
                return CONTACTED_NOTIFICATION;
            else if(key.equals(UPDATE_NOTIFICATION.value))
                return UPDATE_NOTIFICATION;
            else return DELETE_ACCOUNT;
        }
    }
}
