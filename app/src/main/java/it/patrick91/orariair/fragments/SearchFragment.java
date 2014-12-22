package it.patrick91.orariair.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import it.patrick91.orariair.R;

/**
 * Created by patrick on 09/12/14.
 */
public class SearchFragment extends Fragment {

    private static final String[] COUNTRIES = {
            "Stop A",
            "Stop B",
            "Stop C",
            "Stop D",
            "Stop E",
            "Stop G",
            "Stop H",
            "Stop I",
            "Stop J",
            "Stop K",
            "Stop L",
            "Stop M",
    };

    private OnSearchListener mSearchListener;

    public SearchFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mSearchListener = (OnSearchListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Spinner fromSpinner = (Spinner) rootView.findViewById(R.id.from_spinner);
        Spinner toSpinner = (Spinner) rootView.findViewById(R.id.to_spinner);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);

        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        rootView.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchListener != null) {
                    mSearchListener.onSearch("a", "b");
                }
            }
        });

        return rootView;
    }

    public interface OnSearchListener {
        void onSearch(String from, String to);
    }
}