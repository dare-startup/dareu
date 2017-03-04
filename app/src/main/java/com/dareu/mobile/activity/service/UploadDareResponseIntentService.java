package com.dareu.mobile.activity.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.user.DareResponseActivity;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.handler.ApacheResponseHandler;
import com.dareu.mobile.net.request.UploadDareResponseRequest;
import com.dareu.mobile.net.response.ApacheResponseWrapper;
import com.dareu.mobile.utils.NotificationUtils;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.EntityRegistrationResponse;
import com.google.gson.Gson;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jose.rubalcaba on 02/17/2017.
 */

public class UploadDareResponseIntentService extends IntentService{

    private static final String TAG = "UploadResponseService";

    public static final String ACTION = "com.dareu.mobile.activity.service.UploadDareResponseIntentService.action";
    public static final String VIDEO_PATH = "com.dareu.mobile.activity.service.UploadDareResponseIntentService.videoPath";
    public static final String THUMBNAIL_PATH = "com.dareu.mobile.activity.service.UploadDareResponseIntentService.thumbPath";
    public static final String DARE_ID = "com.dareu.mobile.activity.service.UploadDareResponseIntentService.dareId";
    public static final String COMMENT = "com.dareu.mobile.activity.service.UploadDareResponseIntentService.comment";

    private static AtomicInteger currentUploadId = new AtomicInteger(100);
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public UploadDareResponseIntentService() {
        super("UploadDareResponseIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try{
            Intent argIntent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, argIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle("Uploading response")
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setProgress(0, 0, true);
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            //notify
            manager.notify(currentUploadId.get(), builder.build());

            //create request
            UploadDareResponseRequest request = getDareUploadRequest(intent);
            ApacheResponseWrapper wrapper = createRequest(request);

            if(wrapper != null){
                switch(wrapper.getStatusCode()){
                    case 200:
                        EntityRegistrationResponse response = new Gson()
                                .fromJson(wrapper.getJsonResponse(), EntityRegistrationResponse.class);

                        //create intent
                        Intent dareResponseIntent = new Intent(this, DareResponseActivity.class);
                        dareResponseIntent.putExtra(DareResponseActivity.DARE_RESPONSE_ID, response.getId());

                                //update notification
                        builder.setContentTitle("Video response has been uploaded")
                                 .setContentText("Tap to watch your response")
                                 .setAutoCancel(true)
                                 .setProgress(0, 0, false)
                                .setContentIntent(PendingIntent.getActivity(this, 0, dareResponseIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                                .setSmallIcon(R.drawable.ic_notification_icon);
                        manager.notify(currentUploadId.get(), builder.build());
                        break;
                    case 500:
                        //another notification to retry here
                        break;

                }
            }

        }catch(IOException ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    private UploadDareResponseRequest getDareUploadRequest(Intent intent)throws IOException {
        UploadDareResponseRequest request = new UploadDareResponseRequest();
        String dareId = intent.getStringExtra(DARE_ID);
        String videoPath = intent.getStringExtra(VIDEO_PATH);
        String thumbPath = intent.getStringExtra(THUMBNAIL_PATH);
        String comment = intent.getStringExtra(COMMENT);

        request.setComment(comment);
        request.setVideo(SharedUtils.getStreamFromFile(new File(videoPath)));
        request.setThumbImage(SharedUtils.getStreamFromFile(new File(thumbPath)));
        request.setDareid(dareId);

        return request;
    }

    private ApacheResponseWrapper createRequest(UploadDareResponseRequest request){
        String host = SharedUtils.getProperty(PropertyName.DEBUG_SERVER, this);
        String path = SharedUtils.getProperty(PropertyName.UPLOAD_DARE_RESPONSE, this);

        String token = SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN);
        HttpPost post = new HttpPost(host + path);
        post.addHeader("Authorization", token);
        post.addHeader("Accept", "application/json");
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentBody videoPart = new InputStreamBody(request.getVideo(), ContentType.DEFAULT_BINARY);
        entity.addPart("file", videoPart);
        ContentBody thumbPart = new InputStreamBody(request.getThumbImage(), ContentType.DEFAULT_BINARY);
        entity.addPart("thumb", thumbPart);
        ContentBody dareIdPart = new StringBody(request.getDareid(), ContentType.TEXT_PLAIN);
        entity.addPart("dareId", dareIdPart);
        ContentBody commentPart = new StringBody(request.getComment(), ContentType.TEXT_PLAIN);
        entity.addPart("comment", commentPart);


        post.setEntity(entity);


        try{
            return new DefaultHttpClient().execute(post, new ApacheResponseHandler(path));
        }catch(IOException ex){
            Log.e(TAG, ex.getMessage());
            return null;
        }
    }

}
