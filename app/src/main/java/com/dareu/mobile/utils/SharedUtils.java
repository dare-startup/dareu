package com.dareu.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Connection;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.MainActivity;
import com.dareu.mobile.adapter.WelcomeDialogAdapter;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.account.UpdateRegIdTask;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.web.dto.response.UpdatedEntityResponse;
import com.dareu.web.dto.response.message.ConnectionRequestMessage;
import com.dareu.web.dto.response.message.NewDareMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    private static final String TAG = "SharedUtils";

    private static Picasso picassoInstance;

    private static Picasso authenticatedPicassoInstance;

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

    public static void checkFirebaseRegistrationId(final Context cxt) {
        String value = getStringPreference(cxt, PrefName.ALREADY_REGISTERED_GCM_TOKEN);
        if(value != null && ! value.isEmpty()){
            Boolean updated = Boolean.parseBoolean(value);
            if(! updated){
                //get reg id
                String regId = getStringPreference(cxt, PrefName.GCM_TOKEN);
                if(regId != null && ! regId.isEmpty()){
                    //update it
                    UpdateRegIdTask task = new UpdateRegIdTask(cxt, new AsyncTaskListener<UpdatedEntityResponse>() {
                        @Override
                        public void onTaskResponse(UpdatedEntityResponse response) {
                            if(response != null && response.isSuccess()){
                                setStringPreference(cxt, PrefName.ALREADY_REGISTERED_GCM_TOKEN, Boolean.TRUE.toString());
                                Log.i(TAG, response.getMessage());
                            }
                            else{
                                Log.i(TAG, "Something bad just happened :(");
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                    task.execute();
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

    public static InputStream getStreamFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        return in;
    }

    public static InputStream getStreamFromFile(File file)throws IOException{
        FileInputStream stream = new FileInputStream(file);
        return stream;
    }

    public static void setupFirstVisitDialog(final Context cxt) {
        //check if user is for the first time here
        if(SharedUtils.getBooleanPreference(cxt, PrefName.FIRST_TIME)){
            AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
            builder.setCancelable(false);
            //create view
            View welcomeDialogView = LayoutInflater.from(cxt).inflate(R.layout.welcome_dialog, null);

            //get view pager
            ViewPager pager = (ViewPager)welcomeDialogView.findViewById(R.id.welcomeDialogViewPager);

            //create adapter
            pager.setAdapter(new WelcomeDialogAdapter(((AppCompatActivity)cxt).getSupportFragmentManager()));

            //set listener for close label
            TextView closeView = (TextView)welcomeDialogView.findViewById(R.id.welcomeDialogCloseView);

            //set view
            builder.setView(welcomeDialogView);
            //create dialog
            final AlertDialog dialog = builder.create();
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.dimAmount = 0.0f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedUtils.setBooleanPreference(cxt, PrefName.FIRST_TIME, Boolean.FALSE);
                    //close dialog
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private static Picasso instance(Context context){
        if(picassoInstance == null){

        }

        return picassoInstance;
    }

    private static Picasso getAuthenticatedPicassoInstance(Context cxt){
        if(authenticatedPicassoInstance == null){
            //create client
            OkHttpClient client = new OkHttpClient();

            authenticatedPicassoInstance = new Picasso.Builder(cxt)
                    //TODO: change here
                    .build();
        }
        return authenticatedPicassoInstance;
    }


    public static void loadImagePicasso(ImageView imageView, Context context, String uri){
        try{
            RequestCreator creator = instance(context).load(Uri.parse(uri))
                    .config(Bitmap.Config.ARGB_8888)
                    .error(android.R.drawable.stat_notify_error)
                    .placeholder(R.mipmap.ic_launcher);

            creator.fit();

            creator.into(imageView);
        }catch(Exception ex){

        }
    }
}
