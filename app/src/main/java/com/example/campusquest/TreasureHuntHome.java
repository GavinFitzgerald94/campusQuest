package com.example.campusquest;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static com.example.campusquest.CampusQuestDatabaseContract.*;

public class TreasureHuntHome extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>  {
    public static final int LOADER_PREV_GAME = 0;
    private CampusQuestOpenHelper mDbOpenHelper;
    private boolean previousGameFound;
    private String mQuestName;
    private String mQuestId;
    private int mCurrStage = 1;
    private int mTotalStage;
    private int mPrevStage;
    Button mResumeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt_home);
        QuestInfo questInfo = getIntent().getExtras().getParcelable("questInfo");
        mDbOpenHelper = new CampusQuestOpenHelper(this);

        initialiseQuestInfo(questInfo);

        mResumeButton = findViewById(R.id.button_resume_gamebutton);
        mResumeButton.setVisibility(View.GONE);

        getLoaderManager().initLoader(LOADER_PREV_GAME, null, this);

    }

    // Convert to class to make async?
    private boolean checkPreviousGame() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String questId = mQuestId;
        String selection = UserQuestsInfoEntry.COLUMN_QUEST_ID + " = ? AND "
                + UserQuestsInfoEntry.COLUMN_COMPLETED + " == 0";

        String[] selectionArgs = {questId};

        String[] lastQuestColumns = {
                UserQuestsInfoEntry.COLUMN_USERNAME,
                UserQuestsInfoEntry.COLUMN_QUEST_ID,
                UserQuestsInfoEntry.COLUMN_CURRENT_STAGE,
                UserQuestsInfoEntry.COLUMN_COMPLETED};

        Cursor lastQuestCursor = db.query(UserQuestsInfoEntry.TABLE_NAME, lastQuestColumns,
                selection, selectionArgs, null, null, null);

        if (lastQuestCursor.getCount() > 0) {
            int currStagePos = lastQuestCursor.getColumnIndex(UserQuestsInfoEntry.COLUMN_CURRENT_STAGE);
            lastQuestCursor.moveToNext();
            mPrevStage = lastQuestCursor.getInt(currStagePos);
            return true;
        } else {
            return false;
        }
    }

    private void initialiseQuestInfo(QuestInfo questInfo) {
        mQuestName = questInfo.getQuestName();
        mTotalStage =  questInfo.getTotalStages();
        mQuestId = questInfo.getQuestId();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbOpenHelper.close();
    }

    /**
     * Create new game and navigate to treasure hunt game page
     * Passes a bundle consisting of current stage, total stages, questName, questId
     **/
    public void newGame(View view) {
        Intent intent = new Intent(this, TreasureHunt.class);
        Bundle bundle = new Bundle();
        bundle.putInt("currStage", mCurrStage);
        bundle.putInt("totalStage", mTotalStage);
        bundle.putString("questName", mQuestName);
        bundle.putString("questId", mQuestId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Resume existing game
     **/
    public void resumeGame(View view) {
        mCurrStage = mPrevStage;
        Intent intent = new Intent(this, TreasureHuntNoFitTest.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_PREV_GAME)
            loader = createLoaderPrevGame();

        return loader;
    }

    @SuppressLint("StaticFieldLeak")
    private CursorLoader createLoaderPrevGame() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                String questId = mQuestId;
                String selection = UserQuestsInfoEntry.COLUMN_QUEST_ID + " = ? AND "
                        + UserQuestsInfoEntry.COLUMN_COMPLETED + " == 0";

                String[] selectionArgs = {questId};

                String[] lastQuestColumns = {
                        UserQuestsInfoEntry.COLUMN_USERNAME,
                        UserQuestsInfoEntry.COLUMN_QUEST_ID,
                        UserQuestsInfoEntry.COLUMN_CURRENT_STAGE,
                        UserQuestsInfoEntry.COLUMN_COMPLETED};

                return db.query(UserQuestsInfoEntry.TABLE_NAME, lastQuestColumns,
                        selection, selectionArgs, null, null, null);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==LOADER_PREV_GAME)
            loadPrevGameOption(data);
    }

    private void loadPrevGameOption(Cursor data) {
        Cursor lastQuestCursor = data;

        if (lastQuestCursor.getCount() > 0) {
            int currStagePos = lastQuestCursor.getColumnIndex(UserQuestsInfoEntry.COLUMN_CURRENT_STAGE);
            lastQuestCursor.moveToNext();
            mPrevStage = lastQuestCursor.getInt(currStagePos);
            mResumeButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

//    private class getGameDate extends AsyncTask<void, void, void> {
//
//    }
//        protected void  doInBackground(Void... params) {
//    }
//}
}