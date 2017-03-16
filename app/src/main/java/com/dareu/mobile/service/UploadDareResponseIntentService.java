package com.dareu.mobile.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.user.DareResponseActivity;
import com.dareu.mobile.utils.PrefName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.DareClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.request.UploadDareResponseRequest;
import com.dareu.web.dto.response.EntityRegistrationResponse;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jose.rubalcaba on 02/17/2017.
 */

public class UploadDareResponseIntentService extends IntentService{

    private static final String TAG = "UploadResponseService";

    public static final String ACTION = "com.dareu.mobile.service.UploadDareResponseIntentService.action";
    public static final String VIDEO_PATH = "com.dareu.mobile.service.UploadDareResponseIntentService.videoPath";
    public static final String THUMBNAIL_PATH = "com.dareu.mobile.service.UploadDareResponseIntentService.thumbPath";
    public static final String DARE_ID = "com.dareu.mobile.service.UploadDareResponseIntentService.dareId";
    public static final String COMMENT = "com.dareu.mobile.service.UploadDareResponseIntentService.comment";

    private static AtomicInteger currentUploadId = new AtomicInteger(100);

    private DareClientService dareService;

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
            createRequest(request, builder, manager);
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
        request.setVideoPath(videoPath);
        request.setThumbImagePath(thumbPath);
        request.setDareid(dareId);

        return request;
    }

    private void createRequest(UploadDareResponseRequest request, final NotificationCompat.Builder builder, final NotificationManager manager){
        if(dareService == null)
            dareService = RetroFactory.getInstance()
                    .create(DareClientService.class);
        RequestBody filePart = RequestBody.create(MediaType.parse("video/mpeg"), new File(request.getVideoPath()));
        RequestBody thumbPart = RequestBody.create(MediaType.parse("image/jpeg"), new File(request.getThumbImagePath()));
        RequestBody commentPart = RequestBody.create(MediaType.parse("text/plain"), request.getComment());
        RequestBody dareId = RequestBody.create(MediaType.parse("text/plain"), request.getDareid());

        dareService.uploadDareResponse(filePart, thumbPart, dareId, commentPart,
                SharedUtils.getStringPreference(this, PrefName.SIGNIN_TOKEN))
        .enqueue(new Callback<EntityRegistrationResponse>() {
            @Override
            public void onResponse(Call<EntityRegistrationResponse> call, Response<EntityRegistrationResponse> response) {
                //create intent
                Intent dareResponseIntent = new Intent(UploadDareResponseIntentService.this, DareResponseActivity.class);
                switch(response.code()){
                    case 200:
                        dareResponseIntent.putExtra(DareResponseActivity.DARE_RESPONSE_ID, response.body().getId());

                        //update notification
                        builder.setContentTitle("Video response has been uploaded")
                                .setContentText("Tap to watch your response")
                                .setAutoCancel(true)
                                .setProgress(0, 0, false)
                                .setContentIntent(PendingIntent.getActivity(UploadDareResponseIntentService.this, 0, dareResponseIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                                .setSmallIcon(R.drawable.ic_notification_icon);
                        manager.notify(currentUploadId.get(), builder.build());
                        break;
                    case 500:
                        //TODO: show error notification to retry
                        break;
                    case 404:
                        //TODO: show error notification to retry later
                        break;
                }

            }

            @Override
            public void onFailure(Call<EntityRegistrationResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

}
