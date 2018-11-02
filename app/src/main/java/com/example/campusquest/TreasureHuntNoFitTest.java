package com.example.campusquest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import static com.example.campusquest.CampusQuestDatabaseContract.*;

public class TreasureHuntNoFitTest extends AppCompatActivity {
    private CampusQuestOpenHelper mDbOpenHelper;
    private String mQuestName;
    private String mQuestId;
    private int mCurrentStage;
    private int mTotalStage;
    private String mClueText;
    private String mClueId;
    private double mClueLat;
    private double mClueLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt_no_fit_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbOpenHelper = new CampusQuestOpenHelper(this);

        Bundle bundle = getIntent().getExtras();
        mQuestName = bundle.getString("questName");
        mQuestId = bundle.getString("questId");
        mCurrentStage = bundle.getInt("currStage");
        mTotalStage = bundle.getInt("totalStage");

        loadClue();
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

    private void loadClue() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String questId = mQuestId;
        String currStage = String.valueOf(mCurrentStage);
        String selection = CluesInfoEntry.COLUMN_QUEST_ID + " = ? AND "
                + CluesInfoEntry.COLUMN_CLUE_STAGE + " == ?";

        String[] selectionArgs = {questId, currStage};

        String[] clueColumns = {
                CluesInfoEntry.COLUMN_QUEST_ID,
                CluesInfoEntry.COLUMN_CLUE_ID,
                CluesInfoEntry.COLUMN_CLUE_TEXT,
                CluesInfoEntry.COLUMN_CLUE_LAT,
                CluesInfoEntry.COLUMN_CLUE_LONG,
                CluesInfoEntry.COLUMN_CLUE_STAGE};

        Cursor clueCursor = db.query(CluesInfoEntry.TABLE_NAME, clueColumns,
                selection, selectionArgs, null, null, null);

        int clueIdPos = clueCursor.getColumnIndex(CluesInfoEntry.COLUMN_CLUE_ID);
        int clueTextPos = clueCursor.getColumnIndex(CluesInfoEntry.COLUMN_CLUE_TEXT);
        int clueLatPos = clueCursor.getColumnIndex(CluesInfoEntry.COLUMN_CLUE_LAT);
        int clueLongPos = clueCursor.getColumnIndex(CluesInfoEntry.COLUMN_CLUE_LONG);

        if (clueCursor.getCount() > 0) {
            clueCursor.moveToNext();
            mClueId = clueCursor.getString(clueIdPos);
            mClueText = clueCursor.getString(clueTextPos);
            mClueLat = clueCursor.getDouble(clueLatPos);
            mClueLong = clueCursor.getDouble(clueLongPos);
        }
    }

    private void initialiseViewContent() {

        TextView questValue = findViewById(R.id.quest_value);
        questValue.setText(mQuestName);

        TextView currStageValue = findViewById(R.id.curr_stage_value);
        currStageValue.setText(String.valueOf(mCurrentStage));

        TextView totalStageValue  = findViewById(R.id.total_stage_value);
        totalStageValue.setText(String.valueOf(mTotalStage));

        TextView clueValue = findViewById(R.id.clue_value);
        clueValue.setText(mClueText);
    }

    protected void onDestroy() {
        super.onDestroy();
        mDbOpenHelper.close();
    }
}
