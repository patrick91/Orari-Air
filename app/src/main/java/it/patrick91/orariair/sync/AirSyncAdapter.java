package it.patrick91.orariair.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import it.patrick91.orariair.R;
import it.patrick91.orariair.data.AirContract;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;
import static it.patrick91.orariair.data.AirContract.LocalityEntry.*;

/**
 * Created by patrick on 22/12/14.
 */
public class AirSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = AirSyncAdapter.class.getSimpleName();

    public AirSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    private String getPageContent(URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            return buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void syncLocalities() {
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8000/api/localities/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String localitiesJsonString = getPageContent(url);

        if (localitiesJsonString == null) {
            return;
        }

        try {
            JSONArray localitiesArray = new JSONArray(localitiesJsonString);

            Vector<ContentValues> cVVector = new Vector<>(localitiesArray.length());

            for (int i = 0; i < localitiesArray.length(); i++) {
                JSONObject obj = localitiesArray.getJSONObject(i);

                cVVector.add(ParsingUtils.parseLocation(obj));
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                getContext().getContentResolver().delete(LocalityEntry.CONTENT_URI, null, null);
                getContext().getContentResolver().bulkInsert(CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Sync started");

        syncLocalities();
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        }

        return newAccount;
    }
}
