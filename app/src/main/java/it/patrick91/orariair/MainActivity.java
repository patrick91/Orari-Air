package it.patrick91.orariair;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import it.patrick91.orariair.fragments.RoutesFragment;
import it.patrick91.orariair.fragments.SearchFragment;
import it.patrick91.orariair.sync.AirSyncAdapter;


public class MainActivity extends ActionBarActivity implements SearchFragment.OnSearchListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "976425307439";
    private boolean mTwoPane;
    private GoogleCloudMessaging mGCM;
    private String mRegId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.routes_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            if (savedInstanceState == null) {
                // TODO: show something
            }
        } else {
            mTwoPane = false;
        }

        AirSyncAdapter.initializeSyncAdapter(this);

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            mGCM = GoogleCloudMessaging.getInstance(this);
            mRegId = Utils.getRegistrationId(this);

            if (mRegId.isEmpty()) {
                registerInBackground();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearch(long fromId, long toId, long date) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();

            args.putLong(RoutesActivity.FROM_ID_KEY, fromId);
            args.putLong(RoutesActivity.TO_ID_KEY, toId);
            args.putLong(RoutesActivity.DATE_KEY, date);

            RoutesFragment fragment = new RoutesFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.routes_container, fragment)
                    .commit();
        } else {

            Intent searchIntent = new Intent(this, RoutesActivity.class);

            searchIntent.putExtra(RoutesActivity.FROM_ID_KEY, fromId);
            searchIntent.putExtra(RoutesActivity.TO_ID_KEY, toId);
            searchIntent.putExtra(RoutesActivity.DATE_KEY, date);

            startActivity(searchIntent);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }
        return true;
    }

    private void registerInBackground() {
        AsyncTask<Void, Void, Void> registerTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (mGCM == null) {
                        mGCM = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }

                    mRegId = mGCM.register(SENDER_ID);

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    AirSyncAdapter.sendRegistrationIdToBackend(mRegId);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    Utils.storeRegistrationId(MainActivity.this, mRegId);
                } catch (IOException ex) {
                    // If there is an error, don't just keep trying to register.
                }

                return null;
            }
        };

        registerTask.execute(null, null, null);
    }
}
