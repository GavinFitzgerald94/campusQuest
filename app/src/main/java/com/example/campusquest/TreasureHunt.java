package com.example.campusquest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

//import cn.pedant.SweetAlert.SweetAlertDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.campusquest.CampusQuestDatabaseContract.*;
import static com.example.campusquest.CampusQuestDatabaseContract.CluesInfoEntry;
import static com.example.campusquest.CampusQuestDatabaseContract.UserQuestsInfoEntry;
import static com.example.campusquest.DataManager.getInstance;

public class TreasureHunt extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ID = 0;
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0x1001;
    public static String TAG = "";
    public static final int LOADER_CLUE = 0;
    public static final int QUEST_COMPLETED = 1;
    public static final int QUEST_INCOMPLETE = 0;
    private Button mButtonViewToday;
    private float userLat = 53.308498f;
    private float userLng = -6.223649f;
    private CampusQuestOpenHelper mDbOpenHelper;
    private String mQuestName;
    private String mQuestId;
    private int mCurrentStage;
    private int mTotalStage;
    private String mClueText;
    private String mClueId;

    private static final float distanceThreshold = 0.01f;
    private OnDataPointListener mListener;
    private float mClueLat;
    private float mClueLong;
    private float locationABSvalue;
    private GoogleApiClient mGoogleApiClient;
    private Cursor mClueCursor;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    Marker now;

    //Clue locations for faking being at clue
    private float[] mClueLocationsLat = new float[]{53.308400f, 53.306741f, 53.306220f, 53.305928f, 53.308320f};
    private float[] mClueLocationsLng = new float[]{-6.221913f, -6.221380f, -6.220468f, -6.224306f, -6.225765f};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt);        // Set display content
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // Generic toolbar
        setSupportActionBar(toolbar);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

//        mButtonViewToday = (Button) findViewById(R.id.view_today);
//        //Sets listener for onClick event
//        mButtonViewToday.setOnClickListener(this);

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

    }

    /** Callback function to check if permissions are correct */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);//Checks if app can use fine location data as of marshmallow this is required at run-time
            }
        }

    }

    /** Additional callback function to check if runtime permissions are correct */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordData();
                    trackLocation();
                } else {
                    // permission denied, boo!
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /** Callback function for when the map is ready to be used */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check for fine location permission, needs to be checked at run time in order to display map current location to user.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String LOG = "Permissions Error";
            Log.e(TAG,"Do not have user permission for Fine or Coarse location");
            return;
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        float userLat = getUserLat();
        float userLng = getUserLng();
        LatLng user = new LatLng(userLat, userLng);

        //Adds marker at users location.
        // *NOTE* not needed on mobile as "mMap.getUiSettings().setMyLocationButtonEnabled(true);" displays user location but on emulator this will not work so this is used instead.
        now = mMap.addMarker(new MarkerOptions().position(user).title("Your Location"));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(user).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /** Function called when a users location has changed updates map view and marker */
    public void onLocationChanged() {

        //removes google map marker if it exits
        if(now != null){
            now.remove();
        }

        //gets users new latitude and longitude
        float userLat = getUserLat();
        float userLng = getUserLng();
        LatLng user = new LatLng(userLat, userLng);

        //sets marker for users updated location, this is only needed to display location when using a emulator, on mobile displaying location is handled by google map library "mMap.getUiSettings().setMyLocationButtonEnabled(true);"
        now = mMap.addMarker(new MarkerOptions().position(user).title("Your Location"));
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(user).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    /** Called when user location icon is clicked (only on mobile not visible on emulator) */
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    /** Called when user location button is clicked (only on mobile not visible on emulator) */
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    /** Returns current user */
    public String getCurrentUser() {
        DataManager data = getInstance();
        return data.getCurrentUserName();
    }

    public void clueFound() {
        if (mCurrentStage != mTotalStage) {
            mCurrentStage += 1;
            loadCurrentStage(); // loads text view data
            getLoaderManager().restartLoader(LOADER_CLUE, null, this);
            new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Sweet!")
                    .setContentText("You found the next clue.")
                    .setCustomImage(R.drawable.treasure_icon)
                    .show();
        } else {
            String victory = "Quest Completed!";
            mClueText = victory;
            displayClue();
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Good job!")
                    .setContentText("You found the Treasure!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            Intent intent = new Intent(TreasureHunt.this, TreasureHuntHome.class);
                            QuestInfo questInfo = new QuestInfo("QU01", "Treasure Hunt", 5);
                            intent.putExtra("questInfo", questInfo);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
        new UpdateUserInfo().execute();
    }


    /**
     * Creates Recording subscription, this data is recorded constantly in the background in a battery efficient manner.
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

    /** Gets users current stage in quest */
    private void loadCurrentStage() {
        TextView currStageValue = findViewById(R.id.currentStage);
        currStageValue.setText(String.valueOf("Current Stage: "+mCurrentStage));
    }

    /** Sets quest name and total number of stages in quest */
    private void loadViewContent() {
        TextView questValue = findViewById(R.id.questName);
        questValue.setText(mQuestName);
        TextView totalStageValue = findViewById(R.id.totalStageValue);
        totalStageValue.setText(String.valueOf("Number of Stages: "+mTotalStage));
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

//        //Check for distance Raw Data
//        Fitness.getSensorsClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                .findDataSources(
//                        new DataSourcesRequest.Builder()
//                                .setDataTypes(DataType.TYPE_DISTANCE_DELTA)
//                                .setDataSourceTypes(DataSource.TYPE_RAW)
//                                .build())
//                .addOnSuccessListener(
//                        new OnSuccessListener<List<DataSource>>() {
//                            @Override
//                            public void onSuccess(List<DataSource> dataSources) {
//                                for (DataSource dataSource : dataSources) {
//                                    TAG = "Looking for Location";
//                                    Log.i(TAG, "Data source found: " + dataSource.toString());
//                                    Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());
//
//                                    // Let's register a listener to receive location data!
//                                    if (dataSource.getDataType().equals(DataType.TYPE_DISTANCE_DELTA)
//                                            && mListener == null) {
//                                        Log.i(TAG, "Data source for TYPE_DISTANCE_DELTA found!  Registering.");
//                                        //CHANGE
//                                        registerFitnessDataListener(dataSource, DataType.TYPE_DISTANCE_DELTA);
//                                    }
//                                }
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.e(TAG, "failed", e);
//                            }
//                        });
    }

    /**
     * Uses google fits sensors client to add a listener which receives user fitness data
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
                                onLocationChanged();
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
            mClueLat = mClueCursor.getFloat(clueLatPos);
            mClueLong = mClueCursor.getFloat(clueLongPos);
        }

        displayClue();

    }

    private void displayClue() {
        TextView clueValue = findViewById(R.id.clue);
        clueValue.setText(mClueText);
    }

    /**
     *  Update user quest current stage and if at final stage set quest as completed.
     *  Increment appropriate character stats (here, intelligence and endurance) by 1
     *  and if completed increment by 2.
     *  Increment level by 1 if quest completed.
     */

    private class UpdateUserInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues valuesQuest = new ContentValues(0);
            ContentValues valuesCharacter = new ContentValues(0);

            valuesQuest.put(UserQuestsInfoEntry.COLUMN_QUEST_ID, mQuestId);
            valuesQuest.put(UserQuestsInfoEntry.COLUMN_USERNAME, getCurrentUser());
            valuesQuest.put(UserQuestsInfoEntry.COLUMN_CURRENT_STAGE, mCurrentStage);
            // Check if quest is completed.
            if (mCurrentStage == mTotalStage) {
                valuesQuest.put(UserQuestsInfoEntry.COLUMN_COMPLETED, QUEST_COMPLETED);
                valuesCharacter.put(UserCharacterInfoEntry.COLUMN_ENDURANCE, 2);
                valuesCharacter.put(UserCharacterInfoEntry.COLUMN_INTELLIGENCE, 2);
                valuesCharacter.put(UserCharacterInfoEntry.COLUMN_LEVEL, 1);
            } else {
                valuesQuest.put(UserQuestsInfoEntry.COLUMN_COMPLETED, QUEST_INCOMPLETE);
                valuesCharacter.put(UserCharacterInfoEntry.COLUMN_ENDURANCE, 1);
                valuesCharacter.put(UserCharacterInfoEntry.COLUMN_INTELLIGENCE, 1);
            }

            SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
            long questRowId = db.insert(UserQuestsInfoEntry.TABLE_NAME, null, valuesQuest);
            long charRowId = db.insert(UserCharacterInfoEntry.TABLE_NAME, null, valuesCharacter);

            return null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_CLUE) {
            if (mClueCursor != null)
                mClueCursor.close();
        }

    }

    /**
     * Set user latitude
     *
     * @param: latitude, a float representing users latitude
     * */
    public void setUserLat(float latitude) {
        this.userLat = latitude;
    }

    /**
     * Set user longitude
     *
     * @param: longitude, a float representing users longitude
     * */
    public void setUserLng(float longitude) {
        this.userLng = longitude;
    }

    /**
     * Get user latitude
     *
     * @return: Users latitude
     * */
    public float getUserLat() {
        return userLat;
    }

    /**
     * Get user longitude
     *
     * @return: Users longitude
     * */
    public float getUserLng() {
        return userLng;
    }

    /**
     * Function to fake the correct coordinates of user location, to move them to next stage of treasure hunt
     * */
    public void fakeFindClueLocation(View view) {
        String LOG = "Debug";
        Log.e(LOG, "mCurrentStage: "+mCurrentStage);
        switch (mCurrentStage) {
            case 1:  setUserLat(mClueLocationsLat[0]);setUserLng(mClueLocationsLng[0]);
                break;
            case 2:  setUserLat(mClueLocationsLat[1]);setUserLng(mClueLocationsLng[1]);
                break;
            case 3:  setUserLat(mClueLocationsLat[2]);setUserLng(mClueLocationsLng[2]);
                break;
            case 4:  setUserLat(mClueLocationsLat[3]);setUserLng(mClueLocationsLng[3]);
                break;
            case 5:  setUserLat(mClueLocationsLat[4]);setUserLng(mClueLocationsLng[4]);
                break;
        }
        onLocationChanged();
        checkLocation();
    }

    /**
     * Checks the absolute difference between user location and clue location, if below threshold clueFound() is called
     * */
    public void checkLocation() {
        //**NEED TO CHANGE TO GREATER CIRCLE DISTANCE INSTEAD OF ABSOLUTE DIFFERENCE**
        String LOG = "Debug Location Check";
        Log.e(LOG, "mClueLat: "+mClueLat+" mClueLng: "+mClueLong);
        Log.e(LOG, "userLat: "+userLat+" userLng: "+userLng);
        if(mClueLat < 0){
            mClueLat *= -1;
        }
        if(mClueLong < 0){
            mClueLong *= -1;
        }
        if(userLat < 0){
            userLat *= -1;
        }
        if(userLng < 0){
            userLng *= -1;
        }
        if (mClueLat > userLat && mClueLong > userLng) {
             locationABSvalue = (mClueLat - userLat) + (mClueLong - userLng);
        } else if(mClueLat < userLat && mClueLong < userLng){
            locationABSvalue = ( userLat - mClueLat) + (userLng -mClueLong);
        } else if (mClueLat > userLat && mClueLong < userLng){
            locationABSvalue = (mClueLong - userLat) + (userLng -mClueLong);
        }else if (mClueLat < userLat && mClueLong > userLng){
            locationABSvalue = (userLat -mClueLat) + (mClueLong -userLng);
        }else if (mClueLat == userLat && mClueLong == userLng){
            locationABSvalue = (userLat -mClueLat) + (mClueLong -userLng);
        }
        Log.e(LOG, "locationABSvalue: "+locationABSvalue+" distanceThreshold: "+distanceThreshold);
        if(locationABSvalue < distanceThreshold){
            clueFound();
        }
    }

    /**
     * Initialises and executes CountStepsToday on click
     */
    @Override
    public void onClick(View v) {
    }


    /**
     * Google fit api callback method, called when connection has been suspended
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i("HistoryAPI", "onConnectionSuspended");
    }

    /**
     * Google fit api callback method, called when connected has failed
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    /**
     * Google fit api callback method, called when connected successfully
     */
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("HistoryAPI", "onConnected");
    }

    protected void onDestroy() {
        super.onDestroy();
        mDbOpenHelper.close();
    }

}
