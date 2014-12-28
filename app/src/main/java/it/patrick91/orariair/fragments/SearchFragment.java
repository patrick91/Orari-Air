package it.patrick91.orariair.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import it.patrick91.orariair.R;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;

/**
 * Created by patrick on 09/12/14.
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOCALITY_LOADER = 0;
    private static final String SELECTED_FROM_KEY = "selected_from_position";
    private static final String SELECTED_TO_KEY = "selected_to_position";
    private OnSearchListener mSearchListener;

    private static final String[] LOCALITY_COLUMNS = {
            LocalityEntry._ID,
            LocalityEntry.COLUMN_NAME,
            LocalityEntry.COLUMN_API_ID,
    };
    private SimpleCursorAdapter mLocalityAdapter;
    private int mFromPosition = Spinner.INVALID_POSITION;
    private int mToPosition = Spinner.INVALID_POSITION;
    private Spinner mFromSpinner;
    private Spinner mToSpinner;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOCALITY_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mFromSpinner = (Spinner) rootView.findViewById(R.id.from_spinner);
        mToSpinner = (Spinner) rootView.findViewById(R.id.to_spinner);

        mLocalityAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                LOCALITY_COLUMNS,
                new int[]{0, android.R.id.text1}, 0);

        mFromSpinner.setAdapter(mLocalityAdapter);
        mToSpinner.setAdapter(mLocalityAdapter);

        mFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFromPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mToPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rootView.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchListener != null) {
                    mSearchListener.onSearch(mFromSpinner.getSelectedItemId(), mToSpinner.getSelectedItemId());
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_FROM_KEY)) {
                mFromPosition = savedInstanceState.getInt(SELECTED_FROM_KEY);
            }

            if (savedInstanceState.containsKey(SELECTED_TO_KEY)) {
                mToPosition = savedInstanceState.getInt(SELECTED_TO_KEY);
            }
        }

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mFromPosition != Spinner.INVALID_POSITION) {
            outState.putInt(SELECTED_FROM_KEY, mFromPosition);
        }

        if (mToPosition != Spinner.INVALID_POSITION) {
            outState.putInt(SELECTED_TO_KEY, mToPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                LocalityEntry.CONTENT_URI,
                LOCALITY_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLocalityAdapter.swapCursor(data);

        if (mFromPosition != Spinner.INVALID_POSITION) {
            mFromSpinner.setSelection(mFromPosition);
        }

        if (mToPosition != Spinner.INVALID_POSITION) {
            mToSpinner.setSelection(mToPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLocalityAdapter.swapCursor(null);
    }

    public interface OnSearchListener {
        void onSearch(long fromId, long toId);
    }
}