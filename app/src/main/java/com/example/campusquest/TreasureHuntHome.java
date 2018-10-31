package com.example.campusquest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TreasureHuntHome extends AppCompatActivity {
    private CampusQuestOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt_home);
        //mDbOpenHelper = new CampusQuestOpenHelper(this);
    }

    //SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getReadableDatabase(); // sluggish way of accessing database!

    @Override
    protected void onDestroy() {
        //mDbOpenHelper.close();
        super.onDestroy();
    }
}
