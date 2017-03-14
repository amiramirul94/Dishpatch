package com.android.dishpatch.dishpatch.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.ui.Activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Lenovo on 8/18/2016.
 */
public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = MessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        String type = remoteMessage.getData().get("type");

        Log.v(TAG,type);
        String message = remoteMessage.getData().get("message");
        if(type.equals("dispatch"))
        {
            showDispatchNotification(message);
        }

        //showNotification(remoteMessage.getData().get("message"));
    }

    private void showDispatchNotification(String message) {

        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Check if user is logged in
        //Check whether the notification is intended for restaurant or customer

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("New Delivery")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());

    }

    private void showNotification(String message) {

        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Check if user is logged in
        //Check whether the notification is intended for restaurant or customer

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("FCM Test")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }

}
