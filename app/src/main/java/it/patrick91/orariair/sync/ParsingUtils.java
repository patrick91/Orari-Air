package it.patrick91.orariair.sync;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import it.patrick91.orariair.data.AirContract;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;

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
}
