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

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the ocr database.
 */
public class OCRContract {

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the scan table */
    public static final class ScanEntry implements BaseColumns {

        public static final String TABLE_NAME = "scan";

        // File identifier obtained from /v1/upload. Stored as long.
        public static final String COLUMN_FILE_ID = "file_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Description of recognition
        public static final String COLUMN_DESCRIPTION = "description";

        // Language of recognition. Stored as string.
        public static final String COLUMN_LANG = "language";
        // Percent of recognition. Stored as int.
        public static final String COLUMN_PROGRESS = "progress";

        // Recognized text. Stored as String.
        public static final String COLUMN_RESULT_TEXT = "result";

        // Request status from API. Stored as string.
        public static final String COLUMN_STATUS_API = "status";

        // File/Photo URI that recognize.  Stored as string.
        public static final String COLUMN_URI = "file_uri";

        // Page number in the multi-page documents such as PDF, TIFF, DJVU.  Stored as int.
        public static final String COLUMN_PAGE = "page";

        // File name for the multi-page documents such as PDF, TIFF, DJVU.  Stored as str.
        public static final String COLUMN_FILE_NAME = "file_name";
    }
}
