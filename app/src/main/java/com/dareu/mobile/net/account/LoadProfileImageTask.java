package com.dareu.mobile.net.account;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jose.rubalcaba on 02/02/2017.
 */

public class LoadProfileImageTask extends AsyncTask<Void, Void, Bitmap>{

    private AsyncTaskListener<Bitmap> listener;
    private String url;
    private String token;

    public LoadProfileImageTask(Context cxt, String userImageId, AsyncTaskListener<Bitmap> listener){
        this.listener = listener;
        String userId = userImageId == null ? "" : userImageId;
        this.url = SharedUtils.getProperty(PropertyName.DEBUG_SERVER, cxt) +
                SharedUtils.getProperty(PropertyName.LOAD_IMAGE_PROFILE, cxt) + userId;
        this.token = SharedUtils.getStringPreference(cxt, PrefName.SIGNIN_TOKEN);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", token);
            connection.setRequestProperty("Accept", "image/jpeg");
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            //null input stream
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        listener.onTaskResponse(bitmap);
    }
}
