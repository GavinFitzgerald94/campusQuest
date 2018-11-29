package com.example.campusquest;

import android.provider.BaseColumns;

/**
 * This is a collection of static classes that define the SQLite database table
 * creation code.
 *
 * Called during database creation.
 */

public final class CampusQuestDatabaseContract {

    private CampusQuestDatabaseContract() {
    }

    public static final class UserInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "User";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_UNIVERSITY = "university";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_PHONE = "phone";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_PASSWORD + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_UNIVERSITY + " TEXT NOT NULL, " +
                        COLUMN_AGE + " NUMERIC, " +
                        COLUMN_WEIGHT + " NUMERIC, " +
                        COLUMN_PHONE + " TEXT ) ";

    }

    public static final class QuestsInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "Quests";
        public static final String COLUMN_QUEST_ID = "quest_id";
        public static final String COLUMN_QUEST_NAME = "quest_name";
        public static final String COLUMN_TOTAL_STAGES = "total_stages";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_QUEST_ID + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_QUEST_NAME + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_TOTAL_STAGES + " NUMERIC NOT NULL) ";
    }

    public static final class CluesInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "Clues";
        public static final String COLUMN_CLUE_ID = "clue_id";
        public static final String COLUMN_QUEST_ID = "quest_id";
        public static final String COLUMN_CLUE_TEXT = "clue_text";
        public static final String COLUMN_CLUE_LAT = "clue_lat";
        public static final String COLUMN_CLUE_LONG = "clue_long";
        public static final String COLUMN_CLUE_STAGE = "clue_stage";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_CLUE_ID + " TEXT NOT NULL, " +
                        COLUMN_QUEST_ID + " TEXT NOT NULL, " +
                        COLUMN_CLUE_TEXT + " TEXT NOT NULL, " +
                        COLUMN_CLUE_LAT + " NUMERIC, " +
                        COLUMN_CLUE_LONG + " NUMERIC, " +
                        COLUMN_CLUE_STAGE + " NUMERIC)";
    }

    public static final class UserQuestsInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "UserQuests";
        public static final String COLUMN_USERNAME = "user_name";
        public static final String COLUMN_QUEST_ID = "quest_id";
        public static final String COLUMN_CURRENT_STAGE = "current_stage";
        public static final String COLUMN_COMPLETED = "completed";
        public static final String COLUMN_COMPLETION_DATE = "completion_date";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_USERNAME + " TEXT NOT NULL, " +
                        COLUMN_QUEST_ID + " TEXT NOT NULL, " +
                        COLUMN_CURRENT_STAGE + " NUMERIC NOT NULL, " +
                        COLUMN_COMPLETED + " NUMERIC, " +
                        COLUMN_COMPLETION_DATE + " DEFAULT CURRENT_TIMESTAMP)";
    }

    public static final class UserCharacterInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "UserCharacter";
        public static final String COLUMN_USERNAME = "user_name";
        public static final String COLUMN_INTELLIGENCE = "intelligence";
        public static final String COLUMN_STRENGTH = "strength";
        public static final String COLUMN_ENDURANCE = "endurance";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_COMPLETION_DATE = "completion_date";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_USERNAME + " TEXT NOT NULL, " +
                        COLUMN_INTELLIGENCE + " NUMERIC, " +
                        COLUMN_STRENGTH + " NUMERIC, " +
                        COLUMN_ENDURANCE + " NUMERIC, " +
                        COLUMN_LEVEL + " NUMERIC, " +
                        COLUMN_COMPLETION_DATE + " DEFAULT CURRENT_TIMESTAMP)";
    }

}
