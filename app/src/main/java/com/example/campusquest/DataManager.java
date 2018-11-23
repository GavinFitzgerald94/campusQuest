package com.example.campusquest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.campusquest.CampusQuestDatabaseContract.QuestsInfoEntry;

/**
 * Singleton service for accessing and managing app data.
 */

public class DataManager {

    private static DataManager sInstance;
    private List<QuestInfo> mQuests = new ArrayList<>();

    /** Use this static method to access instance to ensure only one instance is created and used.
     */
    public static DataManager getInstance() {

        if (sInstance == null) {
            sInstance = new DataManager();
        }
        return sInstance;
    }

    public String getCurrentUserName() {
        return "Test McTestalot";
    }

    public List<QuestInfo> getQuests() {
        return mQuests;
    }

    /**
     * Returns a list of all available quest types from quest table.
     * @param dbHelper
     */

    public static void loadQuests(CampusQuestOpenHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] questColumns = {
                QuestsInfoEntry.COLUMN_QUEST_NAME,
                QuestsInfoEntry.COLUMN_TOTAL_STAGES,
                QuestsInfoEntry.COLUMN_QUEST_ID};
        final Cursor questCursor = db.query(QuestsInfoEntry.TABLE_NAME, questColumns,
                null, null, null, null, null);
        loadQuestsFromDatabase(questCursor);
    }

    /**
     * Moves through all rows in quest table, creates a list of quest into objects and appends to
     * an array.
     * @param cursor
     */

    public static void loadQuestsFromDatabase(Cursor cursor) {
        int questNamePos = cursor.getColumnIndex(QuestsInfoEntry.COLUMN_QUEST_NAME);
        int totalStagePos = cursor.getColumnIndex(QuestsInfoEntry.COLUMN_TOTAL_STAGES);
        int questIdPos = cursor.getColumnIndex(QuestsInfoEntry.COLUMN_QUEST_ID);

        DataManager dm = getInstance();

        while(cursor.moveToNext()) {
            String questId = cursor.getString(questIdPos);
            String questName = cursor.getString(questNamePos);
            int totalStages = cursor.getInt(totalStagePos);

            QuestInfo quest = new QuestInfo(questId, questName,totalStages);

            dm.mQuests.add(quest);

        }
    }
}
