package it.patrick91.orariair.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by patrick on 23/12/14.
 */
public class AirContract {
    public static final String CONTENT_AUTHORITY = "it.patrick91.orariair";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LOCALITY = "locality";
    public static final String PATH_ROUTE = "route";

    public static final class LocalityEntry implements BaseColumns {
        public static final String TABLE_NAME = "locality";

        public static final String COLUMN_API_ID = "id";
        public static final String COLUMN_NAME = "name";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCALITY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCALITY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCALITY;

        public static Uri buildLocalityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class RouteEntry implements BaseColumns {
        public static final String TABLE_NAME = "route";

        public static final String COLUMN_FROM = "from_id";
        public static final String COLUMN_TO = "to_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_DURATION = "duration";

        public static final String DATE_FORMAT = "%F";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUTE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ROUTE;

        public static Uri buildRoutesUri(long fromId, long toId) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_FROM, String.valueOf(fromId))
                    .appendQueryParameter(COLUMN_TO, String.valueOf(toId))
                    .build();
        }

        public static Uri buildRoutesUri(long fromId, long toId, long date) {
            Time t = new Time();
            t.set(date);

            String datePath = t.format(DATE_FORMAT);

            return CONTENT_URI.buildUpon()
                    .appendPath(datePath)
                    .appendQueryParameter(COLUMN_FROM, String.valueOf(fromId))
                    .appendQueryParameter(COLUMN_TO, String.valueOf(toId))
                    .build();
        }

        public static long getFromIdFromUri(Uri uri) {
            return Long.valueOf(uri.getQueryParameter(COLUMN_FROM));
        }

        public static long getToIdFromUri(Uri uri) {
            return Long.valueOf(uri.getQueryParameter(COLUMN_TO));
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildRouteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
