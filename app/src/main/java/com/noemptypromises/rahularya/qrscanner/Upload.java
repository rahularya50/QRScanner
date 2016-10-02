package com.noemptypromises.rahularya.qrscanner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Upload extends AsyncTask<Context, Void, Context>{
    private static String TAG = "Upload";

    public static String SUCCESS = "SUCC";

    public static String URL_TEMPLATE = "http://ihome.ust.hk/~arya/cgi-bin/db_test.php?tstamp=%s&cID=%s&pID=%s&lat=%s&long=%s&loc=%s&subloc=%s&data=%s";


    public static Boolean uploadDb(Context context)
    {
        List<String> uploaded = new ArrayList<String>(){};

        DbHelper mDbHelper = new DbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DbHelper.FeedEntry.COLUMN_NAME_TIMESTAMP,
                DbHelper.FeedEntry.COLUMN_NAME_COMPANY_ID,
                DbHelper.FeedEntry.COLUMN_NAME_PERSONAL_ID,
                DbHelper.FeedEntry.COLUMN_NAME_LATITUDE,
                DbHelper.FeedEntry.COLUMN_NAME_LONGITUDE,
                DbHelper.FeedEntry.COLUMN_NAME_LOCATION,
                DbHelper.FeedEntry.COLUMN_NAME_SUB_LOCATION,
                DbHelper.FeedEntry.COLUMN_NAME_QR_DATA,
                DbHelper.FeedEntry._ID
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                DbHelper.FeedEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor c = db.query(
                DbHelper.FeedEntry.TABLE_NAME,                    // The table to query
                projection,                                       // The columns to return
                null,                                             // All columns
                null,                                             // All values
                null,                                             // don't group the rows
                null,                                             // don't filter by row groups
                sortOrder                                         // The sort order
        );

        c.moveToPosition(-1);

        Log.d(TAG, "Outputting database contents");

        while (c.moveToNext())
        {
            //Upload files
            Log.d(TAG, c.getString(c.getColumnIndex(DbHelper.FeedEntry.COLUMN_NAME_QR_DATA)));
            if (upload(c)) uploaded.add(c.getString(c.getColumnIndex(DbHelper.FeedEntry._ID)));
            Log.d(TAG, String.valueOf(c.isLast()));
        }

        c.close();

        // Define 'where' part of query.
        String selection = DbHelper.FeedEntry._ID + " IN ( ";
        for (String id : uploaded) {
            selection += "'" + id + "',";
        }
        selection += "'placeholder');";

        Log.d(TAG, selection);

        // Issue SQL statement.
        db.beginTransaction();
        db.delete(DbHelper.FeedEntry.TABLE_NAME, selection, new String[]{});
        db.setTransactionSuccessful();
        db.endTransaction();

        mDbHelper.close();

        Log.d(TAG, "Operations completed");
        return true;
    }

    private static Boolean upload(Cursor c)
    {
        String urlString = String.format(URL_TEMPLATE,
                                         c.getString(0),
                                         c.getString(1),
                                         c.getString(2),
                                         c.getString(3),
                                         c.getString(4),
                                         c.getString(5),
                                         c.getString(6),
                                         c.getString(7));

        Log.d(TAG, urlString);

        InputStream is = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();

            if (conn.getResponseCode() != 200) return false;

            is = conn.getInputStream();
            String returnCode = readStream(is,4);

            if (!returnCode.equals(SUCCESS)) return false;

        } catch (IOException e) {
            return false;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private static String readStream(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    @Override
    protected Context doInBackground(Context[] c) {
        uploadDb(c[0]);
        return c[0];
    }

    @Override
    protected void onPostExecute(Context context)
    {
        Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
    }
}
