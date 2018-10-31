package com.example.campusquest;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private CampusQuestOpenHelper mDbOpenHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbOpenHelper = new CampusQuestOpenHelper(this);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    /** Called when the user taps the Send button */
    public void countSteps(View view) {
        Intent intent = new Intent(this, TreasureHunt.class);
        startActivity(intent);
    }

    /** Navigates use to the treasure hunt game page **/
    public void navigateToTreasureHuntHome(View view) {
        Intent intent = new Intent(this, TreasureHuntHome.class);
        startActivity(intent);
    }

}
