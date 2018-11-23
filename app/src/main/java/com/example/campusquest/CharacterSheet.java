package com.example.campusquest;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mikepenz.materialdrawer.Drawer;

import java.util.Arrays;
import java.util.List;

import static com.example.campusquest.CampusQuestDatabaseContract.UserCharacterInfoEntry;
import static com.example.campusquest.DataManager.getInstance;

public class CharacterSheet extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_STATS = 0;
    private List<String> mAttributes;
    private Spinner mSpinnerAttributes;
    private String mSelectecAttribute;
    private Drawer drawer;
    private CampusQuestOpenHelper mDbOpenHelper;
    private int mLevel;
    private int mStrength;
    private int mEndurance;
    private int mIntelligence;
    private Cursor mStatsCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_sheet);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] arr =  {"Level", "Strength", "Endurance"};
        mAttributes = Arrays.asList(arr);

        mDbOpenHelper = new CampusQuestOpenHelper(this);
        getLoaderManager().initLoader(LOADER_STATS,null, this);

        mSpinnerAttributes = findViewById(R.id.spinner_attributes);
        buildSpinner();
        mSpinnerAttributes.setOnItemSelectedListener(this);

        drawer = DrawerUtil.getDrawer(this,toolbar);

    }

    private void buildSpinner() {
        ArrayAdapter adapterQuests = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mAttributes);
        adapterQuests.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAttributes.setAdapter(adapterQuests);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectecAttribute = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Auto generated stub.

    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_STATS)
            loader = createLoaderStats();
        return loader;
    }

    /**
     * Create database loader for user character table.
     * @return CursorLoader
     */
    @SuppressLint("StaticFieldLeak")
    private CursorLoader createLoaderStats() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                //String name = getCurrentUser();
                String name = "testname"; // retrieve test data
                String selection = UserCharacterInfoEntry.COLUMN_USERNAME+ " = ?";
                String[] selectionArgs = {name};

                String[] statColumns = {
                        UserCharacterInfoEntry.COLUMN_USERNAME,
                        UserCharacterInfoEntry.COLUMN_LEVEL,
                        UserCharacterInfoEntry.COLUMN_INTELLIGENCE,
                        UserCharacterInfoEntry.COLUMN_STRENGTH,
                        UserCharacterInfoEntry.COLUMN_ENDURANCE};

                return db.query(UserCharacterInfoEntry.TABLE_NAME, statColumns,
                        selection, selectionArgs, null, null,  UserCharacterInfoEntry.COLUMN_COMPLETION_DATE + " DESC");
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==LOADER_STATS)
            loadCharStats(data);
    }

    /**
     * Loads player character stats from database
     * @param data
     */

    private void loadCharStats (Cursor data) {
        mStatsCursor = data;

        if (mStatsCursor.getCount() > 0) {
            int levelPos = mStatsCursor.getColumnIndex(UserCharacterInfoEntry.COLUMN_LEVEL);
            int intelPos = mStatsCursor.getColumnIndex(UserCharacterInfoEntry.COLUMN_INTELLIGENCE);
            int strPos = mStatsCursor.getColumnIndex(UserCharacterInfoEntry.COLUMN_STRENGTH);
            int endPos = mStatsCursor.getColumnIndex(UserCharacterInfoEntry.COLUMN_ENDURANCE);

            mStatsCursor .moveToNext();
            mLevel = mStatsCursor.getInt(levelPos);
            mStrength = mStatsCursor.getInt(intelPos);
            mEndurance = mStatsCursor.getInt(strPos);
            mIntelligence = mStatsCursor.getInt(endPos);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_STATS) {
            if (mStatsCursor != null)
                mStatsCursor.close();
        }

    }

    /** Returns current user */
    public String getCurrentUser() {
        DataManager data = getInstance();
        return data.getCurrentUserName();
    }
}
