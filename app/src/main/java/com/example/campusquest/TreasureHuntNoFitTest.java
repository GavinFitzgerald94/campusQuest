package com.example.campusquest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class TreasureHuntNoFitTest extends AppCompatActivity {
    private String mQuestName;
    private String mQuestId;
    private int mCurrentStage;
    private int mTotalStage;
    private String clue = "There's a lady who sure all that glitters is gold... ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt_no_fit_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        mQuestName = bundle.getString("questName");
        mQuestId = bundle.getString("questId");
        mCurrentStage = bundle.getInt("currStage");
        mTotalStage = bundle.getInt("totalStage");

        initialiseViewContent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initialiseViewContent() {

        TextView questValue = findViewById(R.id.quest_value);
        questValue.setText(mQuestName);

        TextView currStageValue = findViewById(R.id.curr_stage_value);
        currStageValue.setText(String.valueOf(mCurrentStage));

        TextView totalStageValue  = findViewById(R.id.total_stage_value);
        totalStageValue.setText(String.valueOf(mTotalStage));

        TextView clueValue = findViewById(R.id.clue_value);
        clueValue.setText(clue);
    }
}
