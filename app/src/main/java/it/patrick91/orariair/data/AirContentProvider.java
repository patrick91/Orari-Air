package it.patrick91.orariair.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;
import static it.patrick91.orariair.data.AirContract.RouteEntry;

/**
 * Created by patrick on 23/12/14.
 */
public class AirContentProvider extends ContentProvider {
    private AirDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int LOCALITY = 100;
    private static final int LOCALITY_ID = 101;

    private static final int ROUTE = 200;
    private static final int ROUTE_WITH_DATE = 201;

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

        matcher.addURI(authority, AirContract.PATH_ROUTE, ROUTE);
        matcher.addURI(authority, AirContract.PATH_ROUTE + "/*", ROUTE_WITH_DATE);

        return matcher;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case LOCALITY:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LocalityEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case LOCALITY_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LocalityEntry.TABLE_NAME,
                        projection,
                        LocalityEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;

            case ROUTE: {
                long fromId = RouteEntry.getFromIdFromUri(uri);
                long toId = RouteEntry.getToIdFromUri(uri);

                String routeSelection = "from_id = ? and to_id = ?";
                String routeSelectionArgs[] = {String.valueOf(fromId), String.valueOf(toId)};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        RouteEntry.TABLE_NAME,
                        projection,
                        routeSelection,
                        routeSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case ROUTE_WITH_DATE: {
                long fromId = RouteEntry.getFromIdFromUri(uri);
                long toId = RouteEntry.getToIdFromUri(uri);

                String date = RouteEntry.getDateFromUri(uri);

                String routeSelection = "from_id = ? and to_id = ? and date = ?";
                String routeSelectionArgs[] = {String.valueOf(fromId), String.valueOf(toId), date};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        RouteEntry.TABLE_NAME,
                        projection,
                        routeSelection,
                        routeSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LOCALITY:
                return LocalityEntry.CONTENT_TYPE;
            case LOCALITY_ID:
                return LocalityEntry.CONTENT_ITEM_TYPE;
            case ROUTE:
                return RouteEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        Uri returnUri;

        long _id;

        switch (match) {
            case LOCALITY:
                _id = db.insert(LocalityEntry.TABLE_NAME, null, values);

                if (_id > 0) {
                    returnUri = LocalityEntry.buildLocalityUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            case ROUTE:
                _id = db.insert(RouteEntry.TABLE_NAME, null, values);

                if (_id > 0) {
                    returnUri = RouteEntry.buildRouteUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int returnCount = 0;

        switch (match) {
            case LOCALITY:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(LocalityEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            case ROUTE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RouteEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case LOCALITY:
                rowsDeleted = db.delete(
                        LocalityEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        return 0;
    }
}