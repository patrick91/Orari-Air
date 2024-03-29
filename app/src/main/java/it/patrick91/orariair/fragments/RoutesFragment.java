package it.patrick91.orariair.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import it.patrick91.orariair.R;
import it.patrick91.orariair.RoutesActivity;
import it.patrick91.orariair.adapters.RoutesAdapter;
import it.patrick91.orariair.sync.AirSyncAdapter;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;
import static it.patrick91.orariair.data.AirContract.RouteEntry;

/**
 * Created by patrick on 09/12/14.
 */
public class RoutesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ROUTE_LOADER = 1;
    private String mFromLocalityName;
    private String mToLocalityName;

    private long mFromId;
    private long mToId;
    private long mDate;

    private static final String[] ROUTE_COLUMNS = {
            RouteEntry._ID,
            RouteEntry.COLUMN_START_TIME,
            RouteEntry.COLUMN_END_TIME,
            RouteEntry.COLUMN_DURATION,
    };

    public static final int COL_START_TIME = 1;
    public static final int COL_END_TIME = 2;
    public static final int COL_DURATION = 3;

    private Uri mRoutesUri;
    private RecyclerView mRoutesView;
    private LinearLayoutManager mLayoutManager;
    private TextView mLoadingTextView;
    private TextView mNoRoutesLayout;
    private RoutesAdapter mAdapter;

    private BroadcastReceiver routeSyncFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(AirSyncAdapter.NO_ROUTES_FOUND)) {
                mLoadingTextView.setVisibility(View.GONE);
                mNoRoutesLayout.setVisibility(View.VISIBLE);
            }
        }
    };

    public RoutesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);

        Bundle arguments = getArguments();

        if (arguments != null) {
            mFromId = arguments.getLong(RoutesActivity.FROM_ID_KEY);
            mToId = arguments.getLong(RoutesActivity.TO_ID_KEY);
            mDate = arguments.getLong(RoutesActivity.DATE_KEY);

            mRoutesUri = RouteEntry.buildRoutesUri(mFromId, mToId, mDate);

            String[] COLUMNS = {
                    LocalityEntry.COLUMN_NAME,
                    LocalityEntry.COLUMN_API_ID,
            };

            Cursor fromLocalityCursor = getActivity().getContentResolver().query(
                    LocalityEntry.buildLocalityUri(mFromId),
                    COLUMNS,
                    null,
                    null,
                    null
            );
            Cursor toLocalityCursor = getActivity().getContentResolver().query(
                    LocalityEntry.buildLocalityUri(mToId),
                    COLUMNS,
                    null,
                    null,
                    null
            );

            if (fromLocalityCursor.moveToFirst()) {
                mFromLocalityName = fromLocalityCursor.getString(0);
            }

            if (toLocalityCursor.moveToFirst()) {
                mToLocalityName = toLocalityCursor.getString(0);
            }

            fromLocalityCursor.close();
            toLocalityCursor.close();
        }

        mLoadingTextView = (TextView) rootView.findViewById(R.id.loading);
        mNoRoutesLayout = (TextView) rootView.findViewById(R.id.no_routes);

        mAdapter = new RoutesAdapter(getActivity(), null);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRoutesView = (RecyclerView) rootView.findViewById(R.id.routes);
        mRoutesView.setHasFixedSize(true);
        mRoutesView.setLayoutManager(mLayoutManager);
        mRoutesView.setAdapter(mAdapter);

        Time t = new Time();
        t.set(mDate);

        String formattedDate = t.format(SearchFragment.DATE_FORMAT);

        View headerView = inflater.inflate(R.layout.header_routes, mRoutesView, false);
        ((TextView) headerView.findViewById(R.id.from)).setText(mFromLocalityName);
        ((TextView) headerView.findViewById(R.id.to)).setText(mToLocalityName);
        ((TextView) headerView.findViewById(R.id.on)).setText(formattedDate);

        //mRoutesView.addHeaderView(headerView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ROUTE_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(routeSyncFinishedReceiver, new IntentFilter(AirSyncAdapter.SYNC_FINISHED));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(routeSyncFinishedReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                mRoutesUri,
                ROUTE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if (data.getCount() == 0) {
            AirSyncAdapter.syncRouteImmediately(getActivity(), mFromId, mToId, mDate);
        } else {
            mNoRoutesLayout.setVisibility(View.GONE);
            mLoadingTextView.setVisibility(View.GONE);
            mRoutesView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}