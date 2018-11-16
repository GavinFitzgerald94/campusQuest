package com.example.campusquest;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.concurrent.TimeUnit;

public class Stats extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0x1001;
    public static String TAG = "";
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        //Add fitnessOptions for permissions, not using this at the moment using Google Fit Client instance instead however may change in future as this is the recommended approach
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            readData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                readData();
            }
        }
    }


    /**
     * Accesses users google fit history and update UI to display it.
     * */
    private class GetData extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            int stepTotal = 0;
            float caloriesTotal = 0;
            float distanceTotal = 0;

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            TAG = "Step Count Delta";
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                stepTotal = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                Log.i(TAG, "Total steps: " + stepTotal);
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }

            PendingResult<DailyTotalResult> caloriesResult = Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_CALORIES_EXPENDED);
            DailyTotalResult totalCaloriesResult = caloriesResult.await(30, TimeUnit.SECONDS);
            TAG = "Calories Expended";
            if (totalCaloriesResult.getStatus().isSuccess()) {
                DataSet caloriesTotalSet = totalCaloriesResult.getTotal();
                caloriesTotal = caloriesTotalSet.isEmpty()
                        ? 0
                        : caloriesTotalSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                Log.i(TAG, "Total Calories Expended" + stepTotal);
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }

            PendingResult<DailyTotalResult> distanceResult = Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_DISTANCE_DELTA);
            DailyTotalResult totalDistanceResult = distanceResult.await(30, TimeUnit.SECONDS);
            TAG = "Distance Delta";
            if (totalDistanceResult.getStatus().isSuccess()) {
                DataSet distanceTotalSet = totalDistanceResult.getTotal();
                distanceTotal = distanceTotalSet.isEmpty()
                        ? 0
                        : distanceTotalSet.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asFloat();
                Log.i(TAG, "Total Distance traveled" + stepTotal);
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }

            final int finalStepTotal = stepTotal;
            final float finalCaloriesTotal = caloriesTotal;
            final float finalDistanceTotal = distanceTotal;

            //Chnange UI components on this thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView steps = (TextView) findViewById(R.id.numberOfSteps);
                    steps.setText(" "+finalStepTotal);
                    TextView calories = (TextView) findViewById(R.id.numberOfCaloriesBurned);
                    calories.setText(" "+ finalCaloriesTotal);
                    TextView distance = (TextView) findViewById(R.id.distanceTraveledNumber);
                    distance.setText(" "+ finalDistanceTotal);
                }
            });

            return null;
        }
    }

    /** Starts new thread to get user fitness data */
    public void readData() {
        new Stats.GetData().execute();
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

}
