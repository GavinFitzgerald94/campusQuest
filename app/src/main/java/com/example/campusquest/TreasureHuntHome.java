package com.example.campusquest;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class TreasureHuntHome extends AppCompatActivity {
    private CampusQuestOpenHelper mDbOpenHelper;
    private boolean previousGameFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt_home);

        // query database to see if user quest info table contains uncompleted treasure hunt entries
        // update previousGameFound to true.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /** Create new game and navigate to treasure hunt game page **/
    public void newGame(View view) {
        Intent intent = new Intent(this, TreasureHuntNoFitTest.class);
        startActivity(intent);
    }

    /** Resume existing game **/
    public void resumeGame(View view) {
        // TODO (call to db, get questname and queststage and pass in params

    }
}
