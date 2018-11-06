package com.example.campusquest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;

import static com.example.campusquest.CampusQuestDatabaseContract.CluesInfoEntry;
import static com.example.campusquest.CampusQuestDatabaseContract.UserQuestsInfoEntry;
import static com.example.campusquest.DataManager.getInstance;

public class TreasureHunt extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ID = 0;
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0x1001;
    public static String TAG = "";
    public static final int LOADER_CLUE = 0;
    public static final int QUEST_COMPLETED = 1;
    public static final int QUEST_INCOMPLETE = 0;
    private Button mButtonViewToday;
    private float userLat;
    private float userLng;
    private CampusQuestOpenHelper mDbOpenHelper;
    private String mQuestName;
    private String mQuestId;
    private int mCurrentStage;
    private int mTotalStage;
    private String mClueText = "some test text";
    private String mClueId;

    private float distanceThreshold;
    private OnDataPointListener mListener;
    private double mClueLat;
    private double mClueLong;
    private GoogleApiClient mGoogleApiClient;
    private Cursor mClueCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt);        // Set display content
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // Generic toolbar
        setSupportActionBar(toolbar);

        mDbOpenHelper = new CampusQuestOpenHelper(this);

        Bundle bundle = getIntent().getExtras();
        mQuestName = bundle.getString("questName");
        mQuestId = bundle.getString("questId");
        mCurrentStage = bundle.getInt("currStage");
        mTotalStage = bundle.getInt("totalStage");

        loadViewContent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mButtonViewToday = (Button) findViewById(R.id.view_today);
        //Sets listener for onClick event
        mButtonViewToday.setOnClickListener(this);

        // Create a Google Fit Client instance.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
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
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1); //Checks if app can use fine location data as of marshmallow this is required at run-time
        }

        //SQL Query to get Quest Name, stage id, current stage, totalStages, clue and set various textViews
        //clueLat = SQL; Query database to get clue latitude
        //clueLng = SQL; Query database to get clue longitude
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);//Checks if app can use fine location data as of marshmallow this is required at run-time
            }
        }

    }

    /**
     * Write stage completion info to user quest table with timestamp.
     */
    public void stageCompleted() {
        updateUserInfo();
    }

    public String getCurrentUser() {
        DataManager data = getInstance();
        return data.getCurrentUserName();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordData();
                    trackLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void updateClueFound(View view) {
        if (mCurrentStage != mTotalStage) {
            mCurrentStage += 1;
            loadCurrentStage();
            getLoaderManager().restartLoader(LOADER_CLUE, null, this);
        } else {
            String victory = "Quest Completed!";
            mClueText = victory;
            displayClue();
        }
        stageCompleted();
    }


    /**
     * Creates Recording subscription, this data is recorded constantly in the background in a battery efficent manner.
     * Records data to users google fit account which can be accessed through the history api later
     */
    private void recordData() {

        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        TAG = "Step Count Delta";
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

        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_CALORIES_EXPENDED)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            TAG = "Calories Expended";
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

        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_DISTANCE_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            TAG = "Distance Delta";
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
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_CLUE, null, this);
        loadCurrentStage();
    }

    private void loadCurrentStage() {
        TextView currStageValue = findViewById(R.id.curr_stage_value);
        currStageValue.setText(String.valueOf(mCurrentStage));
    }


    private void loadViewContent() {
        TextView questValue = findViewById(R.id.quest_value);
        questValue.setText(mQuestName);
        TextView totalStageValue = findViewById(R.id.total_stage_value);
        totalStageValue.setText(String.valueOf(mTotalStage));
        loadCurrentStage();
    }


    /**
     * Adds a listener to track users location
     * Check if sensor for requested data is available first then registers the listener.
     */
    public void trackLocation() {

        //Check if device has location data sensors
        Fitness.getSensorsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .findDataSources(
                        new DataSourcesRequest.Builder()
                                .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                                .setDataSourceTypes(DataSource.TYPE_RAW)
                                .build())
                .addOnSuccessListener(
                        new OnSuccessListener<List<DataSource>>() {
                            @Override
                            public void onSuccess(List<DataSource> dataSources) {
                                for (DataSource dataSource : dataSources) {
                                    TAG = "Looking for Location";
                                    Log.i(TAG, "Data source found: " + dataSource.toString());
                                    Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                                    // Let's register a listener to receive location data!
                                    if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)
                                            && mListener == null) {
                                        Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                                        registerFitnessDataListener(dataSource, DataType.TYPE_LOCATION_SAMPLE);
                                    }
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "failed", e);
                            }
                        });
    }

    /**
     * Uses google fits sensors client to add a listener
     */
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

        mListener =
                new OnDataPointListener() {
                    @Override
                    public void onDataPoint(DataPoint dataPoint) {
                        Log.e(TAG, "FOUND DATA");
                        for (Field field : dataPoint.getDataType().getFields()) {
                            Value val = dataPoint.getValue(field);
                            String compareLat = "latitude";
                            String compareLng = "longitude";
                            String willCheckLocation = "altitude";
                            if (field.getName().equals(compareLat)) {
                                setUserLat(val.asFloat());
                            } else if (field.getName().equals(compareLng)) {
                                setUserLng(val.asFloat());
                            } else if (field.getName().equals(willCheckLocation)) {
                                checkLocation();
                            }
                            Log.i(TAG, "Detected DataPoint field: " + field.getName());
                            Log.i(TAG, "Detected DataPoint value: " + val);
                        }
                    }
                };

        Fitness.getSensorsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .add(
                        new SensorRequest.Builder()
                                .setDataSource(dataSource)
                                .setDataType(dataType)
                                .setSamplingRate(5, TimeUnit.SECONDS)
                                .build(),
                        mListener)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Listener registered!");
                                } else {
                                    Log.e(TAG, "Listener not registered.", task.getException());
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
                        + CluesInfoEntry.COLUMN_CLUE_STAGE + " = ?";

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
        if (loader.getId() == LOADER_CLUE)
            loadNewClue(data);
    }

    private void loadNewClue(Cursor data) {
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

    private void updateUserInfo() {
        ContentValues values = new ContentValues(0);

        values.put(UserQuestsInfoEntry.COLUMN_QUEST_ID, mQuestId);
        values.put(UserQuestsInfoEntry.COLUMN_USERNAME, getCurrentUser());
        values.put(UserQuestsInfoEntry.COLUMN_CURRENT_STAGE, mCurrentStage);
        if (mCurrentStage == mTotalStage) {
            values.put(UserQuestsInfoEntry.COLUMN_COMPLETED, QUEST_COMPLETED);
        } else {
            values.put(UserQuestsInfoEntry.COLUMN_COMPLETED, QUEST_INCOMPLETE);
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long newRowId = db.insert(UserQuestsInfoEntry.TABLE_NAME, null, values);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_CLUE) {
            if (mClueCursor != null)
                mClueCursor.close();
        }

    }

    public void setUserLat(float latitude) {
        this.userLat = latitude;
    }

    public void setUserLng(float longitude) {
        this.userLng = longitude;
    }


    public void checkLocation() {
//        if(clueLat < 0){
//            clueLat * -1;
//        }
//        if(clueLng < 0){
//            clueLng * -1;
//        }
//        if(userLat < 0){
//            userLat * -1;
//        }
//        if(userLng < 0){
//            userLng * -1;
//        }
//        if (clueLat > userLat && clueLng > userLng) {
//            float locationABSvalue = (clueLat - userLat) + (clueLng - userLng);
//        } else if(clueLat < userLat && clueLng < userLng){
//            float locationABSvalue = ( userLat -clueLat) + (userLng -clueLng);
//        } else if (clueLat > userLat && clueLng < userLng){
//            float locationABSvalue = (clueLat - userLat) + (userLng -clueLng);
//        }else if (clueLat < userLat && clueLng > userLng){
//            float locationABSvalue = (userLat -clueLat) + (clueLng -userLng);
//        }
//        if(locationABSvalue < distanceThreshold){
//            if (currentStage == totalStages){
        //***FINISHED**
        //Display some kinds of well done message (Fragment overlaying screen? when exited return to your stats page?)
        //SQL update number of quests completed
//            }
//            float clueLat = SQL; Query database to get next clue latitude
//            float clueLng = SQL; Query database to get next clue longitude
//            clue = SQL; Query database to get next clue
//            SQL; Update database increment user currentStage by 1
//        }
    }

    /**
     * Initialises and executes CountStepsToday on click
     */
    @Override
    public void onClick(View v) {
    }

    /**
     * Called when the user taps the stats button
     */
    public void statsView(View view) {
        Intent intent = new Intent(this, stats.class);
        startActivity(intent);
    }

    /**
     * Display user location for debugging
     */
    public void uLocation(View view) {
        TextView latLng = (TextView) findViewById(R.id.userLocation);
        latLng.setText("Latitude: " + userLat + " Longitude: " + userLng);
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
