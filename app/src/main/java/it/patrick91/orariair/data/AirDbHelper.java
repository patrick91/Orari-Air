package it.patrick91.orariair.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;
import static it.patrick91.orariair.data.AirContract.RouteEntry;

/**
 * Created by patrick on 23/12/14.
 */
public class AirDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "air.db";

    public AirDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_LOCALITY_TABLE = "CREATE TABLE " + LocalityEntry.TABLE_NAME + " (" +
                LocalityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocalityEntry.COLUMN_API_ID + " INTEGER NOT NULL, " +
                LocalityEntry.COLUMN_NAME + " TEXT NOT NULL" +
                ");";

        final String SQL_CREATE_ROUTE_TABLE = "CREATE TABLE " + RouteEntry.TABLE_NAME + " (" +
                RouteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RouteEntry.COLUMN_FROM + " INTEGER NOT NULL, " +
                RouteEntry.COLUMN_TO + " INTEGER NOT NULL, " +
                RouteEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                RouteEntry.COLUMN_TIME + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + RouteEntry.COLUMN_FROM + ") REFERENCES " +
                LocalityEntry.TABLE_NAME + " (" + LocalityEntry._ID + "), " +

                " FOREIGN KEY (" + RouteEntry.COLUMN_TO + ") REFERENCES " +
                LocalityEntry.TABLE_NAME + " (" + LocalityEntry._ID + ")" +

                ");";

        db.execSQL(SQL_CREATE_LOCALITY_TABLE);
        db.execSQL(SQL_CREATE_ROUTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LocalityEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME);
        onCreate(db);
    }
}
