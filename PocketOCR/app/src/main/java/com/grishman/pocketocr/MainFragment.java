package com.grishman.pocketocr;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.grishman.pocketocr.data.OCRContract;
import com.grishman.pocketocr.data.OCRDbHelper;
import com.grishman.pocketocr.data.ScanProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayAdapter<String> mRecognitionAdapter;
    private ScanAdapter mforecastAdapter = null;
    private ListView mListView;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        OCRDbHelper helper = new OCRDbHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(OCRContract.ScanEntry.COLUMN_LANG, "ENG");
        //initialValues.put(DatabaseOpenHelper.DESCRIPTION, "Test2 test2");
        getActivity().getContentResolver().insert(OCRContract.ScanEntry.CONTENT_URI, initialValues);
        //db.insert(DATABASE_TABLE, null, initialValues);
        // Create some dummy data for the ListView.  Here's a sample weekly forecast 
        String[] dummyData = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(dummyData));

        // Now that we have some dummy forecast data, create an ArrayAdapter. 
        // The ArrayAdapter will take data from a source (like our dummy forecast) and 
        // use it to populate the ListView it's attached to. 
        mRecognitionAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_recognitions, // The name of the layout ID.
                        R.id.list_item_recognitions_textview, // The ID of the textview to populate.
                        weekForecast);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mforecastAdapter = new ScanAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mforecastAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
//                    Intent intent = new Intent(getActivity(), DetailsActivity.class)
//                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    ((Callback) getActivity())
                            .onItemSelected(OCRContract.ScanEntry.buildScanFromID( cursor.getLong(COL_WEATHER_DATE)
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
        mforecastAdapter.setUseTodayLayout(mUseTodayLayout);
        return rootView;

        // Get a reference to the ListView, and attach this adapter to it. 
        ListView listView = (ListView) rootView.findViewById(R.id.listview_recognitions);
        listView.setAdapter(mRecognitionAdapter);


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
