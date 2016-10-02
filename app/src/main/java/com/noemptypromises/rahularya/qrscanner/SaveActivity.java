package com.noemptypromises.rahularya.qrscanner;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class SaveActivity extends AppCompatActivity {

    String qrValue;
    Location location;

    TextView latTextView;
    TextView longTextView;
    TextView dataTextView;
    TextView timeTextView;
    Spinner subLocationSpinner;

    final String TAG = "SaveActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        qrValue = intent.getStringExtra("QRValue");
        location = (Location)intent.getExtras().get("location");

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setElements();
        startSpinner();
        setTextViews();
    }

    private void setElements()
    {
        latTextView = (TextView) findViewById(R.id.latTextView);
        longTextView = (TextView) findViewById(R.id.long_text_view);
        dataTextView = (TextView) findViewById(R.id.data_text_view);
        timeTextView = (TextView) findViewById(R.id.time_text_view);

        subLocationSpinner = (Spinner) findViewById(R.id.location_spinner);
    }

    private void setTextViews()
    {
        if (location != null) {
            latTextView.setText(String.valueOf(location.getLatitude()));
            longTextView.setText(String.valueOf(location.getLongitude()));
        }
        else {
            latTextView.setText("Unknown");
            longTextView.setText("Unknown");
        }
        dataTextView.setText(String.valueOf(qrValue));
        timeTextView.setText(String.valueOf(System.currentTimeMillis()));
    }

    private void startSpinner()
    {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        subLocationSpinner.setAdapter(adapter);
    }

    public void submit(View view) {
        Log.d(TAG, "Saving");

        DbHelper mDbHelper = new DbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbHelper.FeedEntry.COLUMN_NAME_LATITUDE, latTextView.getText().toString());
        values.put(DbHelper.FeedEntry.COLUMN_NAME_LONGITUDE, longTextView.getText().toString());
        values.put(DbHelper.FeedEntry.COLUMN_NAME_QR_DATA, dataTextView.getText().toString());
        values.put(DbHelper.FeedEntry.COLUMN_NAME_REMARKS, "remarks!!!");
        values.put(DbHelper.FeedEntry.COLUMN_NAME_SUB_LOCATION, subLocationSpinner.getSelectedItem().toString());
        values.put(DbHelper.FeedEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis() / 1000);
        values.put(DbHelper.FeedEntry.COLUMN_NAME_PERSONAL_ID, "personal_id");
        values.put(DbHelper.FeedEntry.COLUMN_NAME_COMPANY_ID, 1234);
        values.put(DbHelper.FeedEntry.COLUMN_NAME_LOCATION, "Hong Kong (I guess?)");

        db.beginTransaction();
        db.insertOrThrow(DbHelper.FeedEntry.TABLE_NAME, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();

        mDbHelper.close();

        finish();
    }
}
