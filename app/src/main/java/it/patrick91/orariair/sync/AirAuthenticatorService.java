package it.patrick91.orariair.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by patrick on 22/12/14.
 */
public class AirAuthenticatorService extends Service {
    private AirAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new AirAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
