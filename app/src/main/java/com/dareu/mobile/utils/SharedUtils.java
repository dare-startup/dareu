package com.dareu.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by jose.rubalcaba on 10/08/2016.
 */

public class SharedUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");

    private static final String PREFERENCES_NAME = "com.dareu.mobile.utils.SaredUtils.dareuPreferencesName";

    public static final String PROPERTIES_FILE_NAME = "dareu_props.properties";

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


    public static String getProperty(PropertyName name, Context cxt){

        try{
            Properties properties = getProperties(cxt);
            return properties.getProperty(name.toString(), "");
        }catch(Exception ex){
            return "";
        }
    }

    private static Properties getProperties(Context cxt)throws Exception{
        Properties props = new Properties();
        AssetManager manager = cxt.getAssets();
        InputStream in = manager.open(PROPERTIES_FILE_NAME);
        props.load(in);
        return props;
    }

    public static boolean validateDate(String date){
        try{
            Date d = DATE_FORMAT.parse(date);
            return true;
        }catch(ParseException pe){
            return false;
        }
    }

    public static boolean updateGcmTask(Context cxt, String regId){
        String url  = getProperty(PropertyName.DEBUG_SERVER, cxt) + getProperty(PropertyName.UPDATE_GCM_RE_ID, cxt);
        setStringPreference(cxt, PrefName.GCM_TOKEN, regId);
        try{
            String authToken = SharedUtils.getStringPreference(cxt, PrefName.SIGNIN_TOKEN);

            url += regId;
            HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Authorization", authToken);
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();

            if(responseCode == 200)
                return true;
            return false;
        }catch(MalformedURLException e){
            return false;
        }catch(IOException ex){
            return false;
        }
    }

}
