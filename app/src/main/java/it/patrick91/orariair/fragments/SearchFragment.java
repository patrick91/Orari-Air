package it.patrick91.orariair.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import it.patrick91.orariair.R;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;

/**
 * Created by patrick on 09/12/14.
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOCALITY_LOADER = 0;
    private OnSearchListener mSearchListener;

    private static final String[] LOCALITY_COLUMNS = {
            LocalityEntry._ID,
            LocalityEntry.COLUMN_NAME,
            LocalityEntry.COLUMN_API_ID,
    };
    private SimpleCursorAdapter mLocalityAdapter;

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

        Spinner fromSpinner = (Spinner) rootView.findViewById(R.id.from_spinner);
        Spinner toSpinner = (Spinner) rootView.findViewById(R.id.to_spinner);


        mLocalityAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                LOCALITY_COLUMNS,
                new int[]{0, android.R.id.text1}, 0);

        fromSpinner.setAdapter(mLocalityAdapter);
        toSpinner.setAdapter(mLocalityAdapter);

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLocalityAdapter.swapCursor(null);
    }

    public interface OnSearchListener {
        void onSearch(String from, String to);
    }
}