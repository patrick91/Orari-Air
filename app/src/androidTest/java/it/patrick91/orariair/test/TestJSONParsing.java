package it.patrick91.orariair.test;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import it.patrick91.orariair.data.AirContract;
import it.patrick91.orariair.sync.ParsingUtils;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;
import static it.patrick91.orariair.data.AirContract.RouteEntry;

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

    public void testSingleRouteParsing() throws JSONException {
        String routeJSONString = "{" +
            "\"stops\": [{" +
                "\"time\": \"08:00\"," +
                "\"name\": \"AVELLINO - P.zza Kennedy\"" +
            "}, {" +
                "\"time\": \"08:35\"," +
                "\"name\": \"FISCIANO (SA)\"" +
            "}]," +
            "\"start_time\": \"08:00\"," +
            "\"end_time\": \"08:35\"," +
            "\"duration\":\"00:35\"" +
        "}";

        JSONObject obj = new JSONObject(routeJSONString);

        ContentValues values = ParsingUtils.parseRoute(obj);

        assertEquals(values.getAsString(RouteEntry.COLUMN_START_TIME), "08:00");
        assertEquals(values.getAsString(RouteEntry.COLUMN_END_TIME), "08:35");
        assertEquals(values.getAsString(RouteEntry.COLUMN_DURATION), "00:35");
    }
}
