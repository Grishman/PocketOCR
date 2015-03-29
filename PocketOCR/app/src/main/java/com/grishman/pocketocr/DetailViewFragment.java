package com.grishman.pocketocr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailViewFragment extends Fragment {
    /**
     * A placeholder fragment containing a simple view.
     */
    public DetailViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detailview, container, false);
        return rootView;
    }

}
