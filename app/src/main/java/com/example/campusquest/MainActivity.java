package com.example.campusquest;

import android.app.Activity;
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
        Intent intent = new Intent(this, treasure_hunt.class);
        // temporary database test
        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getReadableDatabase(); // sluggish way of accessing database!
        startActivity(intent);
    }

}
