package com.dareu.mobile.utils;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.SignupActivity;
import com.dareu.mobile.service.DareuFirebaseTokenCleanerService;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.entity.AccountProfile;
import com.dareu.web.dto.response.message.ConnectionRequestMessage;
import com.dareu.web.dto.response.message.NewDareMessage;
import com.dareu.web.dto.response.message.QueuedDareMessage;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jose.rubalcaba on 10/08/2016.
 */

public class SharedUtils {

    private static final String TAG = "SharedUtils";
    public static final String SERVICE_PACKAGE = "com.dareu.mobile.service";
    public static final int GOOGLE_SIGNIN_REQUEST_CODE = 289;
    private static Picasso picassoInstance;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    public static final SimpleDateFormat DETAILS_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    private static final String PREFERENCES_NAME = "com.dareu.mobile.utils.SaredUtils.dareuPreferencesName";

    public static final String PROPERTIES_FILE_NAME = "dareu_props.properties";

    public static final String[] TIMERS = new String[]{"1 Hrs", "3 Hrs", "6 Hrs", "12 Hrs"};
    public static File VIDEO_DIRECTORY = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/DareU/");
    public static File IMAGE_DIRECTORY = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/DareU/");

    public static void signout(Context cxt) {
        //delete preferences
        SharedPreferences prefs = cxt.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefs
                .edit()
                .clear()
                .commit();

        //start service to delete firebase registration
        Intent intent = new Intent(cxt, DareuFirebaseTokenCleanerService.class);
        cxt.startService(intent);
        //TODO:delete other stuff here
    }

    public static AccountProfile getCurrentProfile(Context cxt){
        String json = cxt.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getString(PrefName.CURRENT_PROFILE.toString(), "");
        return new Gson().fromJson(json, AccountProfile.class);
    }

    public static void saveCurrentProfile(AccountProfile profile, Context cxt){
        cxt.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(PrefName.CURRENT_PROFILE.toString(), new Gson().toJson(profile))
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

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
        cursor.moveToFirst();

        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String selectedImagePath = cursor.getString(idx);
        cursor.close();

        return selectedImagePath;
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
            if(date == null)return false;
            Date d = DATE_FORMAT.parse(date);
            return true;
        }catch(ParseException pe){
            return false;
        }
    }

    public static ConnectionType checkInternetConnection(Context cxt){
        ConnectivityManager mgr = (ConnectivityManager)cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if(info == null)
            return ConnectionType.NOT_CONNECTED;
        if(! info.isConnected())
            return ConnectionType.NOT_CONNECTED;
        if(! info.isAvailable())
            return ConnectionType.NOT_CONNECTED;
        if(info.getType() == ConnectivityManager.TYPE_MOBILE)
            return ConnectionType.MOBILE;
        else if(info.getType() == ConnectivityManager.TYPE_WIFI)
            return ConnectionType.WIFI;
        else return ConnectionType.NOT_CONNECTED;
    }





    public static void hideKeyboard(View view, Context cxt){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showNoInternetConnectionSnackbar(CoordinatorLayout layout, Context cxt){
        Snackbar.make(layout, cxt.getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                .setAction("Dismiss", null)
                .show();
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

    public static void checkFirebaseRegistrationId(final Context cxt) {
        String value = getStringPreference(cxt, PrefName.ALREADY_REGISTERED_GCM_TOKEN);
        if(value != null && ! value.isEmpty()){
            Boolean updated = Boolean.parseBoolean(value);
            if(! updated){
                //get reg id
                String regId = getStringPreference(cxt, PrefName.GCM_TOKEN);
                if(regId != null && ! regId.isEmpty()){
                    //update it
                    Call<UpdatedEntityResponse> call = RetroFactory.getInstance()
                            .create(AccountClientService.class)
                            .updateFcmId(regId, getStringPreference(cxt, PrefName.SIGNIN_TOKEN));
                    call.enqueue(new Callback<UpdatedEntityResponse>() {
                        @Override
                        public void onResponse(Call<UpdatedEntityResponse> call, Response<UpdatedEntityResponse> response) {
                            if(response != null && response.body().isSuccess()){
                                setStringPreference(cxt, PrefName.ALREADY_REGISTERED_GCM_TOKEN, Boolean.TRUE.toString());
                                Log.i(TAG, response.body().getMessage());
                            }
                            else{
                                Log.i(TAG, "Something bad just happened :(");
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdatedEntityResponse> call, Throwable t) {

                        }
                    });
                }
            }
        }



    }


    public static void saveBitmapToFile(Bitmap bitmap, String path)throws IOException{
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        out.flush();
        out.close();
    }

    private static Picasso instance(Context context){
        if(picassoInstance == null)
            picassoInstance = Picasso.with(context);

        return picassoInstance;
    }


    public static void loadImagePicasso(ImageView imageView, Context context, String uri){
        try{
                instance(context)
                        .load(Uri.parse(uri))
                        .config(Bitmap.Config.ARGB_8888)
                        .error(R.drawable.ic_info_black_24dp)
                        .placeholder(R.drawable.dareu_orange)
                        .fit()
                        .into(imageView);

        }catch(Exception ex){

        }
    }

    public static QueuedDareMessage parseQueuedDareMessage(Map<String, String> data) {
        QueuedDareMessage message = new QueuedDareMessage();
        message.setDareId(data.get("dareId"));
        message.setCreationDate(data.get("creationDate"));
        message.setCurrentDareStatus(data.get("currentDareStatus"));
        return message;
    }

    public static Intent getGoogleSigninIntent(FragmentActivity cxt){
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        //build client
        GoogleApiClient client = new GoogleApiClient.Builder(cxt)
                .enableAutoManage(cxt, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(client);
        return intent;
    }

    public static String getFromDate(String stringDate){
        try{
            Date date = DETAILS_DATE_FORMAT.parse(stringDate);
            Date now = new Date();

            //get difference
            long diffMs = now.getTime() - date.getTime();
            long diffDays = diffMs / (24 * 60 * 60 * 1000);
            long diffHours = diffMs / (60 * 60 * 1000) % 24;
            long diffMinutes = diffMs / (60 * 1000) % 60;
            long diffSeconds = diffMs / 1000 % 60;


            if(diffDays > 0 && diffDays < 2)
                return "A day ago";
            else if(diffDays > 1)
                return diffDays + " days ago";
            else if(diffHours > 0 && diffHours < 2)
                return diffHours + " hour ago";
            else if(diffHours > 1)
                return diffHours + " hours ago";
            else if(diffMinutes > 0 && diffMinutes < 2)
                return  "A minute ago";
            else if(diffMinutes > 1)
                return diffMinutes + " minutes ago";
            else if(diffSeconds > 0)
                return "Seconds ago";
            else return "";
        }catch(ParseException ex){
            return "";
        }
    }

}
