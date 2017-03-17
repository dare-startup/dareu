package com.dareu.mobile.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dareu.mobile.R;
import com.dareu.mobile.activity.MainActivity;
import com.dareu.mobile.activity.shared.PendingRequestsActivity;
import com.dareu.mobile.activity.shared.NewDareDataActivity;
import com.dareu.web.dto.response.message.AbstractMessage;
import com.dareu.web.dto.response.message.ConnectionRequestMessage;
import com.dareu.web.dto.response.message.MessageType;
import com.dareu.web.dto.response.message.NewDareMessage;
import com.dareu.web.dto.response.message.QueuedDareMessage;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jose.rubalcaba on 01/31/2017.
 */

public class NotificationUtils {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static final String TAG = "NotificationUtils";

    public static void dispatchNotification(Context cxt, Map<String, String> data){
        String messageType = data.get(AbstractMessage.MESSAGE_TYPE);
        Log.i(TAG, "Received PUSH notification " + messageType);
        if(messageType.equalsIgnoreCase(MessageType.NEW_DARE.toString())){
            //new dare notification
            newDareNotification(cxt, data);
        }else if(messageType.equalsIgnoreCase(MessageType.CONNECTION_REQUEST.toString())){
            //connection request received from another user
            connectionRequestNotification(cxt, data);
        }else if(messageType.equalsIgnoreCase(MessageType.CONNECT_CONFIRMATION_MESSAGE.toString())){
            //TODO:someone accepted a connection request from this device

        }else if(messageType.equalsIgnoreCase(MessageType.QUEUED_DARE_MESSAGE.toString())){
            //user has a queued dare
            queuedDareNotification(cxt, data);
        } else if(messageType.equalsIgnoreCase(MessageType.CLAPPED_RESPONSE_MESSAGE.toString())){
            //someone clapped a response this user uploaded

        }
    }

    private static void queuedDareNotification(Context cxt, Map<String, String> data) {
        QueuedDareMessage message = SharedUtils.parseQueuedDareMessage(data);
        Intent intent = new Intent();
        //send user to check current active or
        if(message.getCurrentDareStatus().equalsIgnoreCase(QueuedDareMessage.ACTIVE)){
            //there is an active dare right now
            createNotification(cxt, MainActivity.class, "You have dares waiting for you",
                    "A user just dared you, but you must complete an active dare", intent);
        }else {
            //there is a pending dare right now
            createNotification(cxt, MainActivity.class, "You have dares waiting for you",
                    "A user just dared you, but you must confirm a pending dare", intent);
        }

    }

    private static void connectionRequestNotification(Context cxt, Map<String, String> data){
        ConnectionRequestMessage message = SharedUtils.parseConnectionRequestMessage(data);
        Intent intent = new Intent(cxt, PendingRequestsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(PendingRequestsActivity.FRIENDSHIP_ID, message.getFriendshipId()) ;

        String text = message.getUserName() + " want to connect with you";
        createNotification(cxt, PendingRequestsActivity.class, "You have a connection request", text, intent);
    }

    private static void newDareNotification(Context cxt, Map<String, String> data){
        NewDareMessage message = SharedUtils.parseNewDareMessage(data);
        Intent intent = new Intent(cxt, NewDareDataActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(NewDareDataActivity.DARE_ID, message.getDareId());

        Intent newDareIntent = new Intent(MainActivity.ACTION_NEW_DARE);
        newDareIntent.putExtra(MainActivity.NEW_DARE_ID, message.getDareId());
        cxt.sendBroadcast(newDareIntent);
        createNotification(cxt, NewDareDataActivity.class, "You have been dared", message.getDareDescription(), intent);
    }

    private static void createNotification(Context cxt, Class<?> type, String title, String text, Intent intent){
        PendingIntent pendingIntent = PendingIntent.getActivity(cxt, NewDareDataActivity.NEW_DARE_DATA_REQUEST_CODE,
                intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(cxt)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager)cxt.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(atomicInteger.incrementAndGet(), builder.build());
    }
}
