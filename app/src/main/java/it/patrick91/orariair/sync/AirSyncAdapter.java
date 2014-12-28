package it.patrick91.orariair.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
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

import static it.patrick91.orariair.data.AirContract.LocalityEntry;
import static it.patrick91.orariair.data.AirContract.RouteEntry;

/**
 * Created by patrick on 22/12/14.
 */
public class AirSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = AirSyncAdapter.class.getSimpleName();

    public static final String SYNC_FINISHED = "SYNC_FINISHED";
    public static final String NO_ROUTES_FOUND = "NO_ROUTES_FOUND";

    public static final String SYNC_ROUTE_KEY = "SYNC_ROUTE";
    public static final String SYNC_FROM_ID_KEY = "SYNC_FROM_ID";
    public static final String SYNC_TO_ID_KEY = "SYNC_TO_ID";

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

    private void syncRoute(long fromId, long toId) {
        String[] COLUMNS = {
                LocalityEntry.COLUMN_API_ID,
        };

        Cursor fromLocalityCursor = getContext().getContentResolver().query(
                LocalityEntry.buildLocalityUri(fromId),
                COLUMNS,
                null,
                null,
                null
        );

        Cursor toLocalityCursor = getContext().getContentResolver().query(
                LocalityEntry.buildLocalityUri(toId),
                COLUMNS,
                null,
                null,
                null
        );

        boolean routesFound = false;

        if (fromLocalityCursor.moveToFirst() && toLocalityCursor.moveToFirst()) {

            long fromApiId = fromLocalityCursor.getLong(0);
            long toApiId = toLocalityCursor.getLong(0);

            Uri uri = Uri.parse("http://10.0.2.2:8000/api/localities/routes/")
                    .buildUpon()
                    .appendQueryParameter("from_locality", String.valueOf(fromApiId))
                    .appendQueryParameter("to_locality", String.valueOf(toApiId))
                    .build();

            URL url = null;

            try {
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String routesJSON = getPageContent(url);

            try {
                JSONArray routes = new JSONArray(routesJSON);

                Vector<ContentValues> cVVector = new Vector<>(routes.length());

                for (int i = 0; i < routes.length(); i++) {
                    JSONObject obj = routes.getJSONObject(i);

                    ContentValues values = ParsingUtils.parseRoute(obj);

                    values.put(RouteEntry.COLUMN_FROM, fromId);
                    values.put(RouteEntry.COLUMN_TO, toId);
                    values.put(RouteEntry.COLUMN_DATE, "Today");

                    cVVector.add(values);
                }

                if (cVVector.size() > 0) {
                    routesFound = true;

                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);

                    getContext().getContentResolver().bulkInsert(RouteEntry.CONTENT_URI, cvArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent i = new Intent(SYNC_FINISHED);

        if (!routesFound) {
            i.putExtra(NO_ROUTES_FOUND, true);
        }

        getContext().sendBroadcast(i);
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
                getContext().getContentResolver().bulkInsert(LocalityEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Sync started");

        if (extras.containsKey(SYNC_ROUTE_KEY) && extras.getBoolean(SYNC_ROUTE_KEY)) {
            Log.d(LOG_TAG, "wants to sync route");

            long fromId = extras.getLong(SYNC_FROM_ID_KEY, -1);
            long toId = extras.getLong(SYNC_TO_ID_KEY, -1);

            if (fromId != -1 && toId != -1) {
                syncRoute(fromId, toId);
            }
        } else {
            syncLocalities();
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void syncRouteImmediately(Context context, long fromId, long toId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(SYNC_ROUTE_KEY, true);
        bundle.putLong(SYNC_FROM_ID_KEY, fromId);
        bundle.putLong(SYNC_TO_ID_KEY, toId);

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

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
