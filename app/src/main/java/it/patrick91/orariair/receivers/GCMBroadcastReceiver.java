package it.patrick91.orariair.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import it.patrick91.orariair.sync.AirSyncAdapter;

/**
 * Created by patrick on 29/12/14.
 */
public class GCMBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

        String msgType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            switch (msgType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    Log.i("gcm_debug", "Message send error");
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    Log.i("gcm_debug", "Message deleted");
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    if (extras.getString("type").equals("LOCATION_UPDATE")) {
                        AirSyncAdapter.syncImmediately(context);
                    }

                    break;
            }
        }
    }

}