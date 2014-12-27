package it.patrick91.orariair.sync;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import it.patrick91.orariair.data.AirContract;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;
import static it.patrick91.orariair.data.AirContract.RouteEntry;

/**
 * Created by patrick on 24/12/14.
 */
public class ParsingUtils {
    public static ContentValues parseLocation(JSONObject obj) throws JSONException {
        ContentValues values = new ContentValues();

        values.put(LocalityEntry.COLUMN_NAME, obj.getString("name"));
        values.put(LocalityEntry.COLUMN_API_ID, obj.getInt("pk"));

        return values;
    }

    public static ContentValues parseRoute(JSONObject obj) throws JSONException {
        ContentValues values = new ContentValues();

        values.put(RouteEntry.COLUMN_START_TIME, obj.getString("start_time"));
        values.put(RouteEntry.COLUMN_END_TIME, obj.getString("end_time"));
        values.put(RouteEntry.COLUMN_DURATION, obj.getString("duration"));

        return values;
    }
}
