package it.patrick91.orariair.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import it.patrick91.orariair.data.AirContract;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;
import static it.patrick91.orariair.data.AirContract.RouteEntry;

/**
 * Created by patrick on 25/12/14.
 */
public class TestProvider extends AndroidTestCase {
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                LocalityEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                LocalityEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertRead() {
        ContentValues values = TestDb.createAvellinoLocalityValues();

        Uri locationUri = mContext.getContentResolver().insert(LocalityEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                LocalityEntry.buildLocalityUri(locationRowId),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(cursor, values);
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

    public void testInsertRoute() {
        ContentValues values = TestDb.createRouteValues(1, 2);

        Uri routeUri = mContext.getContentResolver().insert(RouteEntry.CONTENT_URI, values);
        long routeId = ContentUris.parseId(routeUri);

        assertTrue(routeId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                RouteEntry.buildRoutesUri(1, 2),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(cursor, values);
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(LocalityEntry.CONTENT_URI);
        assertEquals(LocalityEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(LocalityEntry.buildLocalityUri(1));
        assertEquals(LocalityEntry.CONTENT_ITEM_TYPE, type);


        type = mContext.getContentResolver().getType(RouteEntry.buildRoutesUri(1, 2));
        assertEquals(RouteEntry.CONTENT_TYPE, type);
    }
}
