package com.example.campusquest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

/**
 * Main activiy and home page of the application.
 */

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener{
    private CampusQuestOpenHelper mDbOpenHelper;
    private DrawerLayout drawer;
    private Spinner mSpinnerQuests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbOpenHelper = new CampusQuestOpenHelper(this);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerQuests = findViewById(R.id.spinner_quest);
        DataManager.loadQuests(mDbOpenHelper);
        buildSpinner();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initialiseDisplayContent();
    }

    /**
     * Build and populate quest selection spinner.
     */
    private void buildSpinner() {
        List<QuestInfo> quests = DataManager.getInstance().getQuests();
        ArrayAdapter adapterQuests = new ArrayAdapter(this, android.R.layout.simple_spinner_item, quests);
        adapterQuests.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerQuests.setAdapter(adapterQuests);
        mSpinnerQuests.setOnItemSelectedListener(this);
    }

    @Override
    /**
     * Check which side bar navigation item is selected and initiate action.
     */
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                //code to go to main activity goes here.
                Toast.makeText(this, "Home page clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_friends:
                //code to switch to friends page goes here.
                Toast.makeText(this, "Friends page clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_leader_board:
                //code to switch to leader board goes here.
                Toast.makeText(this, "Leader board clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_sign_out:
                //code to sign out goes here
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;

        // code to change fragments
        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentName()).commit();
    }

    @Override
    /**
     * Override back button pressed to close side navigation drawer first before exiting activity.
     */
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initialiseDisplayContent() {
         // TODO load values for character sheet here.

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

    /** Navigater use to the treasure hunt game page **/
    public void navigateTreasureHuntHome() {
        // Create mock quest object, this should be pulled from DB when treasure hunt is selected from quest list.
        // Implemented with navigation drawer?
        QuestInfo questInfo = new QuestInfo("QU01", "TestObjectName", 5);
        Intent intent = new Intent(this, TreasureHuntHome.class);
        intent.putExtra("questInfo", questInfo);
        startActivity(intent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        QuestInfo selectedQuest = (QuestInfo) parent.getItemAtPosition(position);

        switch (selectedQuest.getQuestId()) {
            case "QU01":
                navigateTreasureHuntHome();
                break;
            case "QU02":
                Toast.makeText(this, "This quest has not been implemented yet.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing - auto-generated stub.
    }
}
