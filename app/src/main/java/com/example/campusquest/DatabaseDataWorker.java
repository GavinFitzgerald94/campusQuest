package com.example.campusquest;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    public DatabaseDataWorker(SQLiteDatabase sqLiteDatabase) {
        mDb = sqLiteDatabase;
    }

    public void insertUsers() {
        insertUser("testName", "testPassword", "UCD", 0, 0, "");
    }

    public void insertQuests() {
        insertQuest("QU01", "Treasure Hunt", 5);
        insertQuest("QU02", "Spy Chase", 5);
    }

    public void insertClues() {
        insertClue("CL01", "QU01", "Go here to celebrate the end of days.", 53.308400f, -6.221913f, 1);
        insertClue("CL02", "QU01", "UCD's sorting hat.", 53.306741f, -6.221380f, 2);
        insertClue("CL03", "QU01", "A modern day tower of babel.", 53.306220f, -6.220468f, 3);
        insertClue("CL04", "QU01", "Tucked away on a little path behind... Alexandria was famous for having one of these, ", 53.305928f, -6.224306f, 4);
        insertClue("CL05", "QU01", "West of dinosaur bones", 53.308320f, -6.225765f, 5);
    }

   public void insertTestStats(){
       insertUserCharacterInfo("testname", 1, 1, 1, 1);
   }



    public void insertUser(String username, String password, String university, int age,
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
        values.put(CampusQuestDatabaseContract.QuestsInfoEntry.COLUMN_QUEST_NAME, questName);
        values.put(CampusQuestDatabaseContract.QuestsInfoEntry.COLUMN_TOTAL_STAGES, totalStages);

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

    private void insertUserCharacterInfo(String userName, int level, int intelligence, int str, int endurance) {
        ContentValues values = new ContentValues();

        values.put(CampusQuestDatabaseContract.UserCharacterInfoEntry.COLUMN_USERNAME, userName);
        values.put(CampusQuestDatabaseContract.UserCharacterInfoEntry.COLUMN_LEVEL, level);
        values.put(CampusQuestDatabaseContract.UserCharacterInfoEntry.COLUMN_INTELLIGENCE, intelligence);
        values.put(CampusQuestDatabaseContract.UserCharacterInfoEntry.COLUMN_ENDURANCE, endurance);
        values.put(CampusQuestDatabaseContract.UserCharacterInfoEntry.COLUMN_STRENGTH, str);

        long newRowId = mDb.insert(CampusQuestDatabaseContract.UserCharacterInfoEntry.TABLE_NAME, null, values);
    }


}
