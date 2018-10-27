package com.example.campusquest;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    public DatabaseDataWorker(SQLiteDatabase sqLiteDatabase) {
        mDb = sqLiteDatabase;
    }

    public void insertSampleUser() {
        insertUser("testName", "testPassword", "UCD", 0, 0, "");

    }

    public void insertQuests() {
        insertQuest("QU01", "Treasure Hunt", 5);

    }

    public void insertClues() {
        insertClue("CL01", "QU01", "Some clue text", 53.324363, -6.267018);
    }

    private void insertUser(String username, String password, String university, int age,
                            double weight, String phone) {
        ContentValues values = new ContentValues();

        values.put(CampusQuestDatabaseContract.UserInfoEntry.COLUMN_USERNAME, username);
        values.put(CampusQuestDatabaseContract.UserInfoEntry.COLUMN_PASSWORD, password);
        values.put(CampusQuestDatabaseContract.UserInfoEntry.COLUMN_UNIVERSITY, university);
        values.put(CampusQuestDatabaseContract.UserInfoEntry.COLUMN_AGE, age);
        values.put(CampusQuestDatabaseContract.UserInfoEntry.COLUMN_WEIGHT, weight);
        values.put(CampusQuestDatabaseContract.UserInfoEntry.COLUMN_PHONE, phone);

        long newRowId = mDb.insert(CampusQuestDatabaseContract.UserInfoEntry.TABLE_NAME, null, values);
    }

    private void insertQuest(String questId, String questName, int totalStages) {
        ContentValues values = new ContentValues();

        values.put(CampusQuestDatabaseContract.QuestsInfoEntry.COLUMN_QUEST_ID, questId);
        values.put(CampusQuestDatabaseContract.QuestsInfoEntry.COLUMN_QUEST_ID, questName);
        values.put(CampusQuestDatabaseContract.QuestsInfoEntry.COLUMN_QUEST_ID, totalStages);

        long newRowId = mDb.insert(CampusQuestDatabaseContract.QuestsInfoEntry.TABLE_NAME, null, values);
    }

    private void insertClue(String clueId, String questId, String clueText, double latitude, double longitude) {

        ContentValues values = new ContentValues();

        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_CLUE_ID, clueId);
        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_QUEST_ID, questId);
        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_CLUE_TEXT, clueText);
        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_CLUE_LAT, latitude);
        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_CLUE_LONG, longitude);

        long newRowId = mDb.insert(CampusQuestDatabaseContract.CluesInfoEntry.TABLE_NAME, null, values);
    }

}
