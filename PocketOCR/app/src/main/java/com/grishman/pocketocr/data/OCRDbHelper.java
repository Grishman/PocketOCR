/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grishman.pocketocr.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.grishman.pocketocr.data.OCRContract.ScanEntry;

/**
 * Manages a local database for scan data.
 */
public class OCRDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "ocr.db";

    public OCRDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SCAN_TABLE = "CREATE TABLE " + ScanEntry.TABLE_NAME + " (" +

                ScanEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                ScanEntry.COLUMN_FILE_ID + " INTEGER, " +
                ScanEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                ScanEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ScanEntry.COLUMN_LANG + " INTEGER NOT NULL," +

                ScanEntry.COLUMN_PROGRESS + " REAL, " +
                ScanEntry.COLUMN_RESULT_TEXT + " TEXT, " +

                ScanEntry.COLUMN_STATUS_API + " TEXT, " +
                ScanEntry.COLUMN_URI + " TEXT, " +
                ScanEntry.COLUMN_PAGE + " INTEGER, " +
                ScanEntry.COLUMN_FILE_NAME + " TEXT " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_SCAN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ScanEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
