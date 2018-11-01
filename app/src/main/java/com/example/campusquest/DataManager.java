package com.example.campusquest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.example.campusquest.CampusQuestDatabaseContract.*;

/**
 * Singleton service for accessing and managing app data.
 */

public class DataManager {

    private static DataManager sInstance;

    /** Use this static method to access instance to ensure only one instance is created and used.
     */
    public static DataManager getsInstance() {

        if (sInstance == null) {
            sInstance = new DataManager();
        }
        return sInstance;
    }

    public static void loadFromDatabase(CampusQuestOpenHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] questColumns = {
                QuestsInfoEntry.COLUMN_QUEST_NAME,
                QuestsInfoEntry.COLUMN_TOTAL_STAGES};
        db.query(QuestsInfoEntry.TABLE_NAME, questColumns, null, null, null, null, null);

        // Clues table

    }

    public static void loadQuestsFromDatabase(Cursor cursor) {
        int questNamePos = cursor.getColumnIndex(QuestsInfoEntry.COLUMN_QUEST_NAME);
        int questStagesPos = cursor.getColumnIndex(QuestsInfoEntry.COLUMN_TOTAL_STAGES);


        DataManager dm = getsInstance();


    }
}
