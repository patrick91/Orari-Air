package it.patrick91.orariair.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import it.patrick91.orariair.data.AirContract;
import it.patrick91.orariair.data.AirDbHelper;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;
import static it.patrick91.orariair.data.AirContract.RouteEntry;

/**
 * Created by patrick on 23/12/14.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public static ContentValues createAvellinoLocalityValues() {
        String testName = "Avellino";
        int testApiId = 1;

        ContentValues values = new ContentValues();
        values.put(LocalityEntry.COLUMN_NAME, testName);
        values.put(LocalityEntry.COLUMN_API_ID, testApiId);

        return values;
    }

    public static ContentValues createFiscianoLocalityValues() {
        String testName = "Fisciano";
        int testApiId = 2;

        ContentValues values = new ContentValues();
        values.put(LocalityEntry.COLUMN_NAME, testName);
        values.put(LocalityEntry.COLUMN_API_ID, testApiId);

        return values;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            String expectedValue = entry.getValue().toString();
            int idx = valueCursor.getColumnIndex(columnName);

            assertFalse(idx == -1);
            assertEquals(expectedValue, valueCursor.getString(idx));
        }

        valueCursor.close();
    }

    @Override
    protected void setUp() throws Exception {
        mContext.deleteDatabase(AirDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new AirDbHelper(this.mContext).getWritableDatabase();

        db.delete(LocalityEntry.TABLE_NAME, null, null);
        db.delete(RouteEntry.TABLE_NAME, null, null);

        db.close();
    }

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(AirDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new AirDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        SQLiteDatabase db = new AirDbHelper(this.mContext).getWritableDatabase();

        ContentValues values = createAvellinoLocalityValues();

        long localityRowId = db.insert(LocalityEntry.TABLE_NAME, null, values);

        assertTrue(localityRowId != -1);

        String[] columns = {
                LocalityEntry.COLUMN_NAME,
                LocalityEntry.COLUMN_API_ID,
        };

        Cursor cursor = db.query(LocalityEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        validateCursor(cursor, values);

        db.close();
    }

    public void testInsertRoute() {
        SQLiteDatabase db = new AirDbHelper(this.mContext).getWritableDatabase();

        ContentValues values = createAvellinoLocalityValues();
        ContentValues valuesFisciano = createFiscianoLocalityValues();

        long localityAvellino = db.insert(LocalityEntry.TABLE_NAME, null, values);
        long localityFisciano = db.insert(LocalityEntry.TABLE_NAME, null, valuesFisciano);

        assertTrue(localityAvellino != -1);
        assertTrue(localityFisciano != -1);

        ContentValues routeValues = new ContentValues();
        routeValues.put(RouteEntry.COLUMN_FROM, localityAvellino);
        routeValues.put(RouteEntry.COLUMN_TO, localityFisciano);
        routeValues.put(RouteEntry.COLUMN_DATE, "2014-12-26");
        routeValues.put(RouteEntry.COLUMN_START_TIME, "12:00");
        routeValues.put(RouteEntry.COLUMN_END_TIME, "13:00");
        routeValues.put(RouteEntry.COLUMN_DURATION, "1:00");

        long routeId = db.insert(RouteEntry.TABLE_NAME, null, routeValues);

        assertTrue(routeId != -1);

        db.close();
    }
}
