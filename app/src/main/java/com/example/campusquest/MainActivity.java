package com.example.campusquest;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CampusQuestOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbOpenHelper = new CampusQuestOpenHelper(this);
        initialiseDisplayContent();

    }

    private void initialiseDisplayContent() {
        DataManager.loadQuests(mDbOpenHelper);
        List<QuestInfo> quests = DataManager.getInstance().getQuests();
        //TODO display list of quests!
        //displayQuests()

    }

    //TODO: Convert to Navigation View
    private void displayQuests() {

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
    public void navigateTreasureHuntHome(View view) {
        // Create mock quest object, this should be pulled from DB when treasure hunt is selected from quest list.
        // Implemented with navigation drawer?
        QuestInfo questInfo = new QuestInfo("QU01", "TestObjectName", 5);
        Intent intent = new Intent(this, TreasureHuntHome.class);
        intent.putExtra("questInfo", questInfo);
        startActivity(intent);
    }

}
