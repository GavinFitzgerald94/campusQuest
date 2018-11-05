package com.example.campusquest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.campusquest.CampusQuestDatabaseContract.*;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

public class TreasureHunt extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ID = 0;
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0x1001;
    public static final String TAG = "StepCounter";
    public static final int LOADER_CLUE = 0;
    private Button mButtonViewToday;
    private double userLat;
    private double userLng;
    private CampusQuestOpenHelper mDbOpenHelper;
    private String mQuestName;
    private String mQuestId;
    private int mCurrentStage;
    private int mTotalStage;
    private String mClueText;
    private String mClueId;
    private double mClueLat;
    private double mClueLong;
    private GoogleApiClient mGoogleApiClient;
    private Cursor mClueCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt);        //Set display content
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //Generic toolbar
        setSupportActionBar(toolbar);

        mDbOpenHelper = new CampusQuestOpenHelper(this);

        Bundle bundle = getIntent().getExtras();
        mQuestName = bundle.getString("questName");
        mQuestId = bundle.getString("questId");
        mCurrentStage = bundle.getInt("currStage");
        mTotalStage = bundle.getInt("totalStage");

        getLoaderManager().initLoader(LOADER_CLUE, null, this);
        initialiseViewContent();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Button to read steps data
        mButtonViewToday = (Button) findViewById(R.id.view_today);
        //Sets listener for onClick event
        mButtonViewToday.setOnClickListener(this);

        // Create a Google Fit Client instance.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        //Add fitnessOptions for permissions, not using this at the moment using Google Fit Client instance instead however may change in future as this is the recommended approach
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            recordSteps();
        }

        //SQL Query to get Quest Name, stage id and clue and set various textViews
    }


    private void initialiseViewContent() {

        TextView questValue = findViewById(R.id.quest_value);
        questValue.setText(mQuestName);

        TextView currStageValue = findViewById(R.id.curr_stage_value);
        currStageValue.setText(String.valueOf(mCurrentStage));

        TextView totalStageValue = findViewById(R.id.total_stage_value);
        totalStageValue.setText(String.valueOf(mTotalStage));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                recordSteps();
            }
        }
    }

    /**
     * Creates Recording subscription
     */
    private void recordSteps() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                        } else {
                            Log.w(TAG, "There was a problem subscribing.");
                        }
                    }
                });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_CLUE)
            loader = createLoaderClue();

        return loader;
    }

    @SuppressLint("StaticFieldLeak")
    private CursorLoader createLoaderClue() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                String questId = mQuestId;
                String currStage = String.valueOf(mCurrentStage);
                String selection = CluesInfoEntry.COLUMN_QUEST_ID + " = ? AND "
                        + CluesInfoEntry.COLUMN_CLUE_STAGE + " == ?";

                String[] selectionArgs = {questId, currStage};

                String[] clueColumns = {
                        CluesInfoEntry.COLUMN_QUEST_ID,
                        CluesInfoEntry.COLUMN_CLUE_ID,
                        CluesInfoEntry.COLUMN_CLUE_TEXT,
                        CluesInfoEntry.COLUMN_CLUE_LAT,
                        CluesInfoEntry.COLUMN_CLUE_LONG,
                        CluesInfoEntry.COLUMN_CLUE_STAGE};

                return db.query(CluesInfoEntry.TABLE_NAME, clueColumns,
                        selection, selectionArgs, null, null, null);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==LOADER_CLUE)
            loadFinishedNotes(data);

    }

    private void loadFinishedNotes(Cursor data) {
        mClueCursor = data;

        int clueIdPos = mClueCursor.getColumnIndex(CluesInfoEntry.COLUMN_CLUE_ID);
        int clueTextPos = mClueCursor.getColumnIndex(CluesInfoEntry.COLUMN_CLUE_TEXT);
        int clueLatPos = mClueCursor.getColumnIndex(CluesInfoEntry.COLUMN_CLUE_LAT);
        int clueLongPos = mClueCursor.getColumnIndex(CluesInfoEntry.COLUMN_CLUE_LONG);

        if (mClueCursor.getCount() > 0) {
            mClueCursor.moveToNext();
            mClueId = mClueCursor.getString(clueIdPos);
            mClueText = mClueCursor.getString(clueTextPos);
            mClueLat = mClueCursor.getDouble(clueLatPos);
            mClueLong = mClueCursor.getDouble(clueLongPos);
        }

        displayClue();

    }

    private void displayClue() {
        TextView clueValue = findViewById(R.id.clue_value);
        clueValue.setText(mClueText);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_CLUE) {
            if(mClueCursor != null)
                mClueCursor.close();
        }

    }

//    private void displayStepDataForToday() {
//        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( mGoogleApiClient, DataType.AGGREGATE_STEP_COUNT_DELTA ).await(1, TimeUnit.MINUTES);
//        showDataSet(result.getTotal());
//    }

//    private void showDataSet(DataSet dataSet) {
//        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
//        DateFormat dateFormat = DateFormat.getDateInstance();
//        DateFormat timeFormat = DateFormat.getTimeInstance();
//
//        for (DataPoint dp : dataSet.getDataPoints()) {
//            Log.e("History", "Data point:");
//            Log.e("History", "\tType: " + dp.getDataType().getName());
//            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
//            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
//            for(Field field : dp.getDataType().getFields()) {
//                Log.e("History", "\tField: " + field.getName() +
//                        " Value: " + dp.getValue(field));
//            }
//        }
//    }

//    private class ViewTodaysStepCountTask extends AsyncTask<Void, Void, Void> {
//        protected Void doInBackground(Void... params) {
//            displayStepDataForToday();
//            return null;
//        }
//    }

    /**
     * Accesses google fit history and displays total steps onscreen.
     */
    private class CountStepsToday extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            long total = 0;

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                total = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                final long finalTotal = total;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView steps = (TextView) findViewById(R.id.steps);
                        steps.setText("" + finalTotal);
                    }
                });

            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }

            Log.i(TAG, "Total steps: " + total);

            return null;
        }
    }

    /**
     * Initialises and executes CountStepsToday on click
     */
    @Override
    public void onClick(View v) {
        new CountStepsToday().execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.i("HistoryAPI", "onConnected");
    }

    protected void onDestroy() {
        super.onDestroy();
        mDbOpenHelper.close();
    }

}
