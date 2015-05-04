package com.grishman.pocketocr.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.grishman.pocketocr.data.OCRContract.ScanEntry;

public class ScanProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = ScanProvider.class.getSimpleName();
    private OCRDbHelper mOpenHelper;

    static final int SCAN = 100;
    static final int SCAN_ID = 101;
    private SQLiteQueryBuilder sScanQueryBuilder;

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = OCRContract.CONTENT_AUTHORITY;
        mURIMatcher.addURI(authority, OCRContract.PATH_SCAN, SCAN);
        mURIMatcher.addURI(authority, OCRContract.PATH_SCAN + "/*", SCAN_ID);
        return mURIMatcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new OCRDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "scan/*"
            case SCAN_ID: {
                retCursor = getIDFromNigga(uri, projection, sortOrder, selection);
                break;
            }
            // "scan"
            case SCAN: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScanEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getIDFromNigga(Uri uri, String[] projection, String sortOrder, String selection) {
        long startDate = ScanEntry.getIDFromUri(uri);

        String[] selectionArgs;

        selectionArgs = new String[]{Long.toString(startDate)};
        selection = selection + "_id = " + startDate;

        return sScanQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SCAN_ID:
                return ScanEntry.CONTENT_ITEM_TYPE;
            case SCAN:
                return ScanEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(LOG_TAG, "insert, " + uri.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SCAN: {
                long _id = db.insert(ScanEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = ScanEntry.buildScanUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Fix problem
//        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
