package com.example.campusquest;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.campusquest.CampusQuestDatabaseContract.UserCharacterInfoEntry;
import static com.example.campusquest.DataManager.getInstance;

public class CharacterSheet extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_STATS = 0;
    public static final int ATTRIBS = 3;
    private Spinner mSpinner;
    private String mSelected;
    private Drawer drawer;
    private AccountHeader header;
    private CampusQuestOpenHelper mDbOpenHelper;
    private int mLevel;
    private int mStrength;
    private int mEndurance;
    private int mIntelligence;
    private Cursor mStatsCursor;
    private RadarChart mChart;
    private ImageView avatarImg;
    private List<String> mRaces = Arrays.asList("Gunslinger", "Zelda", "Ninja", "Wizard");
    private android.support.v7.widget.Toolbar mToolbar;

    /**
     * Loads page data including the radar chart used to display information about character attributes.
     * The radar chart is taken from a library called MPCharting - example code available @ https://github.com/PhilJay/MPAndroidChart
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_sheet);

        mToolbar= findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDbOpenHelper = new CampusQuestOpenHelper(this);
        getLoaderManager().initLoader(LOADER_STATS, null, this);

        mSpinner = findViewById(R.id.spinner);
        buildSpinner();
        mSpinner.setOnItemSelectedListener(this);
        setSpinnerSelection();

        // Set current user name
        TextView name = findViewById(R.id.username_label);
        name.setText(getCurrentUser());

        // Set current user image for avatar
        avatarImg = findViewById(R.id.avatar);
        avatarImg.setImageResource(DataManager.getInstance().getCurrentProfilePic());

        // Get drawer
        drawer = DrawerUtil.getDrawer(this, mToolbar);
        header = DrawerUtil.getHeader(this);

        // Create and configure radar chart for displaying stats.
        mChart = findViewById(R.id.stats_chart);
        mChart.setBackgroundColor(Color.rgb(60, 65, 82));
        mChart.getDescription().setEnabled(false);
        mChart.setWebLineWidth(1f);
        mChart.setWebColor(R.color.white);
        mChart.setWebColorInner(R.color.white);
        mChart.setWebAlpha(100);

        mChart.animateXY(1400, 1400, Easing.EaseInOutQuad, Easing.EaseInOutQuad);

        // Customise x axis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(20f);
        xAxis.setYOffset(0);
        xAxis.setXOffset(0);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            // Define attributes
            private String[] attribs = new String[]{"Strength", "Intelligence", "Endurance"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return attribs[(int) value % attribs.length];
            }
        });

        xAxis.setTextColor(Color.WHITE);

        // Customise y axis
        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(ATTRIBS, false);
        yAxis.setTextSize(20f);
        // add to define max and min for y
//        yAxis.setAxisMinimum();
//        yAxis.setAxisMaximum();
        yAxis.setDrawLabels(false);

        // Disable chart legend
        mChart.getLegend().setEnabled(false);

        setRadarData();

    }

    /**
     * Determine which element to select from spinner based on previous selection
     * Retrieves stored selection from Data Manager.
     */

    private void setSpinnerSelection() {
        String name = DataManager.getInstance().getCurrentClass();

        for (String race:mRaces) {
            if (race.equals(name)) {
                mSpinner.setSelection(mRaces.indexOf(race));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.radar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshValues:
                setRadarData(); // or call loader again here
                mChart.invalidate();
                return true;
            case R.id.toggleValues:
                // toggle values on radar chart
                for (IDataSet<?> set : mChart.getData().getDataSets()) {
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }
                mChart.invalidate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Set attribute data to populate attribute radar chart
     * This function is called after the loader has received all attribute data
     * from the db cursor.
     */

    private void setRadarData() {
        ArrayList<RadarEntry> entries = new ArrayList<>();

        entries.add(new RadarEntry(mEndurance));
        entries.add(new RadarEntry(mIntelligence));
        entries.add(new RadarEntry(mStrength));

        RadarDataSet set = new RadarDataSet(entries, "Attributes");
        set.setColor(Color.GREEN);
        set.setFillColor(Color.GREEN);
        set.setDrawFilled(true);
        set.setFillAlpha(180);
        set.setLineWidth(2f);
        set.setDrawHighlightCircleEnabled(true);
        set.setDrawHighlightIndicators(false);

        // Create radar data object to add to chart
        RadarData data = new RadarData(set);
        data.setValueTextSize(10f);
        data.setDrawValues(true);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
        ;
    }

    /**
     * Populates the spinner with data.
     */

    private void buildSpinner() {
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, mRaces);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    /**
     * Changes the avatar picture on item selected.
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelected = (String) parent.getItemAtPosition(position);
        DataManager.getInstance().setCurrentClass(mSelected);
        // Change avatar to selected item.
        switch (mSelected) {
            case "Gunslinger":
                avatarImg.setImageResource(R.drawable.gunslinger);
                DataManager.getInstance().setCurrentProfilePic(R.drawable.gunslinger);
                break;
            case "Zelda":
                avatarImg.setImageResource(R.drawable.elf);
                DataManager.getInstance().setCurrentProfilePic(R.drawable.elf);
                break;
            case "Ninja":
                avatarImg.setImageResource(R.drawable.ninja);
                DataManager.getInstance().setCurrentProfilePic(R.drawable.ninja);
                break;
            case "Wizard":
                avatarImg.setImageResource(R.drawable.wizard);
                DataManager.getInstance().setCurrentProfilePic(R.drawable.wizard);
                break;
        }
        //Recreate drawer to update player profile icon.
        drawer = DrawerUtil.getDrawer(this, mToolbar);
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
     *
     * @return CursorLoader
     */
    @SuppressLint("StaticFieldLeak")
    private CursorLoader createLoaderStats() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                String name = getCurrentUser();
                //String name = "testname"; // retrieve test data
                String selection = UserCharacterInfoEntry.COLUMN_USERNAME + " = ?";
                String[] selectionArgs = {name};

                String[] statColumns = {
                        UserCharacterInfoEntry.COLUMN_USERNAME,
                        UserCharacterInfoEntry.COLUMN_LEVEL,
                        UserCharacterInfoEntry.COLUMN_INTELLIGENCE,
                        UserCharacterInfoEntry.COLUMN_STRENGTH,
                        UserCharacterInfoEntry.COLUMN_ENDURANCE};

                return db.query(UserCharacterInfoEntry.TABLE_NAME, statColumns,
                        selection, selectionArgs, null, null, UserCharacterInfoEntry.COLUMN_COMPLETION_DATE + " DESC");
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_STATS)
            loadCharStats(data);
    }

    /**
     * Loads player character stats from database
     *
     * @param data
     */
    private void loadCharStats(Cursor data) {
        mStatsCursor = data;

        if (mStatsCursor.getCount() > 0) {
            int levelPos = mStatsCursor.getColumnIndex(UserCharacterInfoEntry.COLUMN_LEVEL);
            int intelPos = mStatsCursor.getColumnIndex(UserCharacterInfoEntry.COLUMN_INTELLIGENCE);
            int strPos = mStatsCursor.getColumnIndex(UserCharacterInfoEntry.COLUMN_STRENGTH);
            int endPos = mStatsCursor.getColumnIndex(UserCharacterInfoEntry.COLUMN_ENDURANCE);

            mStatsCursor.moveToNext();
            mLevel = mStatsCursor.getInt(levelPos);
            mStrength = mStatsCursor.getInt(intelPos);
            mEndurance = mStatsCursor.getInt(strPos);
            mIntelligence = mStatsCursor.getInt(endPos);

            // Display stats on graph.
            setRadarData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_STATS) {
            if (mStatsCursor != null)
                mStatsCursor.close();
        }

    }

    /**
     * Returns current user
     */
    public String getCurrentUser() {
        DataManager data = getInstance();
        return data.getCurrentUserName();
    }

}
