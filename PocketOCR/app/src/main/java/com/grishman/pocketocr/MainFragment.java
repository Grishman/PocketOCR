package com.grishman.pocketocr;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.grishman.pocketocr.data.OCRContract;
import com.grishman.pocketocr.data.OCRDbHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1 ;
    private ArrayAdapter<String> mRecognitionAdapter;
    private ScanAdapter mforecastAdapter = null;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    private static final String[] SCAN_COLUMNS = {
// In this case the id needs to be fully qualified with a table name, since
// the content provider joins the location & weather tables in the background
// (both have an _id column)
            OCRContract.ScanEntry.TABLE_NAME + "." + OCRContract.ScanEntry._ID,
            OCRContract.ScanEntry.COLUMN_FILE_ID,
            OCRContract.ScanEntry.COLUMN_DATE,
            OCRContract.ScanEntry.COLUMN_DESCRIPTION,
            OCRContract.ScanEntry.COLUMN_LANG,
            OCRContract.ScanEntry.COLUMN_PROGRESS,
            OCRContract.ScanEntry.COLUMN_RESULT_TEXT,
            OCRContract.ScanEntry.COLUMN_STATUS_API,
            OCRContract.ScanEntry.COLUMN_URI,
            OCRContract.ScanEntry.COLUMN_PAGE,
            OCRContract.ScanEntry.COLUMN_FILE_NAME
    };

    // Indexes
    public static final int COL_SCAN_ID = 0;
    public static final int COL_FILE_ID = 1;
    public static final int COL_DATE = 2;
    public static final int COL_DESC = 3;
    public static final int COL_LANG = 4;
    public static final int COL_PROGRESS = 5;
    public static final int COL_RESULT_TEXT= 6;
    public static final int COL_STATUS = 7;
    public static final int COL_URI = 8;
    public static final int COL_PAGE= 9;
    public static final int COL_FILENAME = 10;
    public MainFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        OCRDbHelper helper = new OCRDbHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(OCRContract.ScanEntry.COLUMN_LANG, "ENG");
        //getActivity().getContentResolver().insert(OCRContract.ScanEntry.CONTENT_URI, initialValues);
        //db.insert(DATABASE_TABLE, null, initialValues);
        // Create some dummy data for the ListView.  Here's a sample weekly forecast 
//        String[] dummyData = {
//                "Mon 6/23 - Sunny - 31/17",
//                "Tue 6/24 - Foggy - 21/8",
//                "Wed 6/25 - Cloudy - 22/17",
//                "Thurs 6/26 - Rainy - 18/11",
//                "Fri 6/27 - Foggy - 21/10",
//                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
//                "Sun 6/29 - Sunny - 20/7"
//        };
//        List<String> weekForecast = new ArrayList<String>(Arrays.asList(dummyData));
//
//        // Now that we have some dummy forecast data, create an ArrayAdapter.
//        // The ArrayAdapter will take data from a source (like our dummy forecast) and
//        // use it to populate the ListView it's attached to.
//        mRecognitionAdapter =
//                new ArrayAdapter<String>(
//                        getActivity(), // The current context (this activity)
//                        R.layout.list_recognitions, // The name of the layout ID.
//                        R.id.list_item_recognitions_textview, // The ID of the textview to populate.
//                        weekForecast);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mforecastAdapter = new ScanAdapter(getActivity(), null, 0);
        //View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_recognitions);
        mListView.setAdapter(mforecastAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
//                    String locationSetting = Utility.getPreferredLocation(getActivity());
//                    Intent intent = new Intent(getActivity(), DetailsActivity.class)
//                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    ((Callback) getActivity())
                            .onItemSelected(OCRContract.ScanEntry.buildScanFromID(cursor.getLong(COL_SCAN_ID)
                            ));
//                    startActivity(intent);
                }
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet. Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        //mforecastAdapter.setUseTodayLayout(mUseTodayLayout);
        return rootView;

        // Get a reference to the ListView, and attach this adapter to it. 
       // ListView listView = (ListView) rootView.findViewById(R.id.listview_recognitions);
       // listView.setAdapter(mRecognitionAdapter);


        //return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

// Sort order: Ascending, by id.
        String sortOrder = OCRContract.ScanEntry._ID + " ASC";
        Uri weatherForLocationUri = OCRContract.ScanEntry.CONTENT_URI;
// Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
// null, null, null, sortOrder);
        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                SCAN_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mforecastAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mforecastAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

}
