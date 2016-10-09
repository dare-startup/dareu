package com.dareu.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jose.rubalcaba on 10/08/2016.
 */

public class SharedUtils {

    private static final String PREFERENCES_NAME = "com.dareu.mobile.utils.SaredUtils.dareuPreferencesName";

    /**
     * Get a String from shared preferences
     * @param cxt
     * @param prefName
     * @return
     */
    public static String getStringPreference(Context cxt, PrefName prefName){
        SharedPreferences prefs = cxt.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getString(prefName.toString(), "");
    }

    public static void setStringPreference(Context cxt, PrefName prefName, String value){
        SharedPreferences prefs = cxt.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(prefName.toString(), value)
                .commit();

    }


    public static enum PrefName{
        SIGNIN_TOKEN("com.dareu.mobile.utils.SahredUtils.signinToken");

        String value;
        PrefName(String value){
            this.value = value;
        }

        @Override
        public String toString(){
            return this.value;
        }
    }
}
