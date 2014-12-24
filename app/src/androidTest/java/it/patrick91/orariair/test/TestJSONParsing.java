package it.patrick91.orariair.test;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import it.patrick91.orariair.sync.ParsingUtils;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;

/**
 * Created by patrick on 24/12/14.
 */
public class TestJSONParsing extends AndroidTestCase {
    public void testSingleLocationParsing() throws JSONException {
        String locationJSONString = "{\"name\": \"Avellino\", \"pk\": 12}";

        JSONObject obj = new JSONObject(locationJSONString);

        ContentValues values = ParsingUtils.parseLocation(obj);

        assertEquals(values.getAsString(LocalityEntry.COLUMN_NAME), "Avellino");
        assertEquals(values.getAsInteger(LocalityEntry.COLUMN_API_ID).intValue(), 12);
    }
}
