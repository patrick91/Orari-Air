package it.patrick91.orariair.test;

import android.content.ContentUris;
import android.content.ContentValues;
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

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
    }
}
