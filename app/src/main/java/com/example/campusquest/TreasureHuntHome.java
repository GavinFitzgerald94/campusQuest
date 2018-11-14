package com.example.campusquest;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mikepenz.materialdrawer.Drawer;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.campusquest.CampusQuestDatabaseContract.UserQuestsInfoEntry;

//import cn.pedant.SweetAlert.SweetAlertDialog;

public class TreasureHuntHome extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>  {
    public static final int LOADER_PREV_GAME = 0;
    private CampusQuestOpenHelper mDbOpenHelper;
    private String mQuestName;
    private String mQuestId;
    private int mCurrStage = 1;
    private int mTotalStage;
    private int mPrevStage;
    private Drawer drawer;
    Button mResumeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_hunt_home);
        QuestInfo questInfo = getIntent().getExtras().getParcelable("questInfo");
        mDbOpenHelper = new CampusQuestOpenHelper(this);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = DrawerUtil.getDrawer(this, toolbar);

        initialiseQuestInfo(questInfo);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumeButton = findViewById(R.id.button_resume_gamebutton);
        mResumeButton.setVisibility(View.GONE);
        getLoaderManager().restartLoader(LOADER_PREV_GAME, null, this);
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

    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Create new game and navigate to treasure hunt game page
     * Passes a bundle consisting of current stage, total stages, questName, questId
     **/
    public void newGame(View view) {
        this.mCurrStage = 1;
        this.mPrevStage = 1;
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
     * Check if there is a existing game, confirm with user they want to erase this data and start a new game.
     **/
    public void resumeGamePopUp(View v) {
        String LOG = "Degbug";
        Log.e(LOG, "mCurrStage "+mPrevStage);
        if(mPrevStage == 1 || mPrevStage == 5){
            newGame(findViewById(R.id.button_resume_gamebutton));
        } else {
            //Sweet Alert Dialog
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("You won't be able to recover your previous game!")
                    .setConfirmText("Yes!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            newGame(findViewById(R.id.button_resume_gamebutton));
                        }
                    })
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
    }


    /**
     * Resume existing game
     **/
    public void resumeGame(View view) {
        int currStage = mPrevStage;
        Intent intent = new Intent(this, TreasureHunt.class);
        Bundle bundle = new Bundle();
        bundle.putInt("currStage", currStage );
        bundle.putInt("totalStage", mTotalStage);
        bundle.putString("questName", mQuestName);
        bundle.putString("questId", mQuestId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Navigate to the Stats page
     **/
    public void navigateToStats(View view) {
        Intent intent = new Intent(this, Stats.class);
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
                String selection = UserQuestsInfoEntry.COLUMN_QUEST_ID + " = ?";

                String[] selectionArgs = {questId};

                String[] lastQuestColumns = {
                        UserQuestsInfoEntry.COLUMN_USERNAME,
                        UserQuestsInfoEntry.COLUMN_QUEST_ID,
                        UserQuestsInfoEntry.COLUMN_CURRENT_STAGE,
                        UserQuestsInfoEntry.COLUMN_COMPLETION_DATE,
                        UserQuestsInfoEntry.COLUMN_COMPLETED};

                return db.query(UserQuestsInfoEntry.TABLE_NAME, lastQuestColumns,
                        selection, selectionArgs, null, null,  UserQuestsInfoEntry.COLUMN_COMPLETION_DATE + " DESC");
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
            int questCompletedPos = lastQuestCursor.getColumnIndex(UserQuestsInfoEntry.COLUMN_COMPLETED);
            lastQuestCursor.moveToNext();
            mPrevStage = lastQuestCursor.getInt(currStagePos);
            int completedFlag = lastQuestCursor.getInt(questCompletedPos);

            // Sets resume button to visible only if the most recent entry was not flagged completed.
            if (completedFlag < 1)
                mResumeButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}