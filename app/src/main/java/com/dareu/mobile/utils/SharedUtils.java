package com.dareu.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.telecom.Connection;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.web.dto.response.message.ConnectionRequestMessage;
import com.dareu.web.dto.response.message.NewDareMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jose.rubalcaba on 10/08/2016.
 */

public class SharedUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");

    private static final String PREFERENCES_NAME = "com.dareu.mobile.utils.SaredUtils.dareuPreferencesName";

    public static final String PROPERTIES_FILE_NAME = "dareu_props.properties";

    public static final String[] TIMERS = new String[]{ "1 Hrs", "3 Hrs", "6 Hrs", "12 Hrs" };


    public static void signout(Context cxt){
        //delete preferences
        SharedPreferences prefs = cxt.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefs
                .edit()
                .clear()
                .commit();
    }

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

    public static void setBooleanPreference(Context cxt, PrefName name, Boolean value){
        SharedPreferences prefs = cxt.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(name.toString(), value)
                .commit();
    }

    public static Boolean getBooleanPreference(Context cxt, PrefName prefName){
        SharedPreferences prefs = cxt.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(prefName.toString(), Boolean.TRUE);
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

    public static boolean checkInternetConnection(Context cxt){
        ConnectivityManager manager = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = manager.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    public static void hideKeyboard(View view, Context cxt){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showNoInternetConnectionSnackbar(CoordinatorLayout layout){
        Snackbar.make(layout, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Accept", null);
        return;
    }

    public static NewDareMessage parseNewDareMessage(Map<String, String> data){
        NewDareMessage message = new NewDareMessage();
        message.setChallenger(data.get("challenger"));
        message.setDareDescription(data.get("dareDescription"));
        message.setDareId(data.get("dareId"));
        message.setDareName(data.get("dareName"));
        message.setTimer(Integer.parseInt(data.get("timer")));
        return message;
    }

    public static ConnectionRequestMessage parseConnectionRequestMessage(Map<String, String> data) {
        ConnectionRequestMessage message = new ConnectionRequestMessage();
        message.setFriendshipId(data.get("friendshipId"));
        message.setRequestUserId(data.get("requestUserId"));
        message.setUserName(data.get("userName"));
        return message;
    }

    public static String getErrorMessage(ApacheResponseWrapper wrapper){
        if(wrapper == null)
            return "No response received from server, try again";
        switch(wrapper.getStatusCode()){
            case 200:
                return "Success";
            case 404:
                return "Server temporarily out of business, try again later";
            case 500:
                return "Something bad has happened, try again";
            case 415:
                return "Someone zap out this shitty developer";
            case 401:
                return "You are not authorized to view this content";
            default:
                return "N/A";
        }
    }




}
