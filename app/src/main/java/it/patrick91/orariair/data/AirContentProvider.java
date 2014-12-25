package it.patrick91.orariair.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;

/**
 * Created by patrick on 23/12/14.
 */
public class AirContentProvider extends ContentProvider {
    private AirDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int LOCALITY = 100;
    private static final int LOCALITY_ID = 101;

    @Override
    public boolean onCreate() {
        mOpenHelper = new AirDbHelper(getContext());

        return true;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AirContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AirContract.PATH_LOCALITY, LOCALITY);
        matcher.addURI(authority, AirContract.PATH_LOCALITY + "/#", LOCALITY_ID);

        return matcher;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LOCALITY:
                return LocalityEntry.CONTENT_TYPE;
            case LOCALITY_ID:
                return LocalityEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LOCALITY:
                long _id = db.insert(LocalityEntry.TABLE_NAME, null, values);

                if (_id > 0) {
                    return LocalityEntry.buildLocalityUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        return 0;
    }
}