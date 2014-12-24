package it.patrick91.orariair.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import it.patrick91.orariair.data.AirContract;
import it.patrick91.orariair.data.AirDbHelper;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;

/**
 * Created by patrick on 23/12/14.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(AirDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new AirDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        String testName = "Avellino";
        int testApiId = 1;

        SQLiteDatabase db = new AirDbHelper(this.mContext).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocalityEntry.COLUMN_NAME, testName);
        values.put(LocalityEntry.COLUMN_API_ID, testApiId);

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

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(LocalityEntry.COLUMN_NAME));
            int apiId = cursor.getInt(cursor.getColumnIndex(LocalityEntry.COLUMN_API_ID));

            assertEquals(name, testName);
            assertEquals(apiId, testApiId);
        } else {
            fail("No rows returned!");
        }

        db.close();
    }
}
