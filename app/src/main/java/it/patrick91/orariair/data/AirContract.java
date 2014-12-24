package it.patrick91.orariair.data;

import android.provider.BaseColumns;

/**
 * Created by patrick on 23/12/14.
 */
public class AirContract {
    public static final class LocalityEntry implements BaseColumns {
        public static final String TABLE_NAME = "locality";

        public static final String COLUMN_API_ID = "id";
        public static final String COLUMN_NAME = "name";
    }
}
