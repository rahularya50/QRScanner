package com.noemptypromises.rahularya.qrscanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Rahul Arya on 024, Sep, 24.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "cache.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String LATLONG_TYPE = " varchar(20)";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_COMPANY_ID + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_PERSONAL_ID + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LATITUDE + LATLONG_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LONGITUDE + LATLONG_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_SUB_LOCATION + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_QR_DATA + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_REMARKS + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "cache";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_COMPANY_ID = "company_id";
        public static final String COLUMN_NAME_PERSONAL_ID = "personal_id";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_SUB_LOCATION = "sublocation";
        public static final String COLUMN_NAME_QR_DATA = "qrdata";
        public static final String COLUMN_NAME_REMARKS = "remarks";
    }
}