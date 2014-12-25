package it.patrick91.orariair.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import it.patrick91.orariair.data.AirContract;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;

/**
 * Created by patrick on 25/12/14.
 */
public class TestProvider extends AndroidTestCase {
    public void testInsert() {
        ContentValues values = TestDb.createAvellinoLocalityValues();

        Uri locationUri = mContext.getContentResolver().insert(LocalityEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);
    }

    public void testBulkInsertRead() {
        ContentValues value = TestDb.createAvellinoLocalityValues();

        ContentValues[] values = new ContentValues[1];
        values[0] = value;

        int rows = mContext.getContentResolver().bulkInsert(LocalityEntry.CONTENT_URI, values);

        assertEquals(rows, 1);

        Cursor cursor = mContext.getContentResolver().query(
                LocalityEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, value);
    }
}
