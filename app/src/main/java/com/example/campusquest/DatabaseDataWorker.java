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
        insertQuest("QU02", "Spy Chase", 5);
    }

    public void insertClues() {
        insertClue("CL01", "QU01", "There's a lady who's sure...(O Reilly)", 53.308400, -6.221913, 1);
        insertClue("CL02", "QU01", "All that glitters is gold...(registry) ", 53.306741, -6.221380, 2);
        insertClue("CL03", "QU01", "And she's buying a stairway to heaven.(language centre)", 53.306220, -6.220468, 3);
        insertClue("CL04", "QU01", "When she get's there she knows...(little path James J", 53.305928, -6.224306, 4);
        insertClue("CL05", "QU01", "If the stores are all closed...(Sci West)", 53.308320, -6.225765, 5);
    }

    public void insertUserQuestInfo() {
        insertUserQuestInfo("testName", "QU01", 3, 0, "");
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

    private void insertClue(String clueId, String questId, String clueText, double latitude, double longitude, int stage) {

        ContentValues values = new ContentValues();

        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_CLUE_ID, clueId);
        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_QUEST_ID, questId);
        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_CLUE_TEXT, clueText);
        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_CLUE_LAT, latitude);
        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_CLUE_LONG, longitude);
        values.put(CampusQuestDatabaseContract.CluesInfoEntry.COLUMN_CLUE_STAGE, stage);

        long newRowId = mDb.insert(CampusQuestDatabaseContract.CluesInfoEntry.TABLE_NAME, null, values);
    }

    private void insertUserQuestInfo(String userName, String questId, int currStage, int completed, String completionDate) {

        ContentValues values = new ContentValues();

        values.put(CampusQuestDatabaseContract.UserQuestsInfoEntry.COLUMN_USERNAME, userName);
        values.put(CampusQuestDatabaseContract.UserQuestsInfoEntry.COLUMN_QUEST_ID, questId);
        values.put(CampusQuestDatabaseContract.UserQuestsInfoEntry.COLUMN_CURRENT_STAGE, currStage);
        values.put(CampusQuestDatabaseContract.UserQuestsInfoEntry.COLUMN_COMPLETED, completed);
        values.put(CampusQuestDatabaseContract.UserQuestsInfoEntry.COLUMN_COMPLETION_DATE, completionDate);

        long newRowId = mDb.insert(CampusQuestDatabaseContract.UserQuestsInfoEntry.TABLE_NAME, null, values);
    }


}
