package com.example.campusquest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.campusquest.CampusQuestDatabaseContract;

public class CampusQuestOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CampusQuest.db";
    public static final int DATEBASE_VERSION = 1;

    public CampusQuestOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATEBASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CampusQuestDatabaseContract.UserInfoEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(CampusQuestDatabaseContract.QuestsInfoEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(CampusQuestDatabaseContract.UserQuestsInfoEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(CampusQuestDatabaseContract.CluesInfoEntry.SQL_CREATE_TABLE);

        DatabaseDataWorker worker = new DatabaseDataWorker(sqLiteDatabase);
        worker.insertSampleUser();
        worker.insertQuests();
        worker.insertClues();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //TODO 

    }
}
