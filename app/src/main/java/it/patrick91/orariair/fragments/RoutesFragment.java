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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private ListView mRoutesView;
    private LinearLayout mLoadingLayout;
    private LinearLayout mNoRoutesLayout;
    private RoutesAdapter mAdapter;

    private BroadcastReceiver routeSyncFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(AirSyncAdapter.NO_ROUTES_FOUND)) {
                mLoadingLayout.setVisibility(View.GONE);
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

            mRoutesUri = RouteEntry.buildRoutesUri(mFromId, mToId);

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
        }

        mLoadingLayout = (LinearLayout) rootView.findViewById(R.id.loading_layout);
        mNoRoutesLayout = (LinearLayout) rootView.findViewById(R.id.no_routes_layout);

        mAdapter = new RoutesAdapter(getActivity(), null, 0);

        mRoutesView = (ListView) rootView.findViewById(R.id.list_view);
        mRoutesView.setAdapter(mAdapter);

        View headerView = inflater.inflate(R.layout.header_routes, mRoutesView, false);
        ((TextView) headerView.findViewById(R.id.from)).setText(getString(R.string.from_xxx, mFromLocalityName));
        ((TextView) headerView.findViewById(R.id.to)).setText(getString(R.string.to_xxx, mToLocalityName));

        mRoutesView.addHeaderView(headerView);

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
            AirSyncAdapter.syncRouteImmediately(getActivity(), mFromId, mToId);
        } else {
            mNoRoutesLayout.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.GONE);
            mRoutesView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}