package it.patrick91.orariair.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.patrick91.orariair.R;
import it.patrick91.orariair.RoutesActivity;
import it.patrick91.orariair.data.AirContract;

import static it.patrick91.orariair.data.AirContract.LocalityEntry;

/**
 * Created by patrick on 09/12/14.
 */
public class RoutesFragment extends Fragment {
    private String mFromLocalityName;
    private String mToLocalityName;

    public RoutesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);

        Bundle arguments = getArguments();

        if (arguments != null) {
            long fromId = arguments.getLong(RoutesActivity.FROM_ID_KEY);
            long toId = arguments.getLong(RoutesActivity.TO_ID_KEY);

            String[] COLUMNS = {
                    LocalityEntry.COLUMN_NAME,
                    LocalityEntry.COLUMN_API_ID,
            };

            Cursor fromLocalityCursor = getActivity().getContentResolver().query(
                    LocalityEntry.buildLocalityUri(fromId),
                    COLUMNS,
                    null,
                    null,
                    null
            );
            Cursor toLocalityCursor = getActivity().getContentResolver().query(
                    LocalityEntry.buildLocalityUri(toId),
                    COLUMNS,
                    null,
                    null,
                    null
            );

            if (fromLocalityCursor.moveToFirst()) {
                mFromLocalityName = fromLocalityCursor.getString(0);
            }

            if (toLocalityCursor.moveToFirst()) {
                mToLocalityName = fromLocalityCursor.getString(0);
            }
        }


        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        RoutesAdapter adapter = new RoutesAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public class RoutesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int ITEM_VIEW_TYPE_HEADER = 1;
        private int ITEM_VIEW_TYPE_ROUTE = 2;

        public class RouteViewHolder extends RecyclerView.ViewHolder {
            public TextView numberView;

            public RouteViewHolder(View itemView) {
                super(itemView);

                numberView = (TextView) itemView.findViewById(R.id.number);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView fromView;
            public TextView toView;

            public ViewHolder(View v) {
                super(v);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return ITEM_VIEW_TYPE_HEADER;
            }

            return ITEM_VIEW_TYPE_ROUTE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            if (viewType == ITEM_VIEW_TYPE_HEADER) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_routes, parent, false);

                ViewHolder vh = new ViewHolder(v);

                vh.fromView = (TextView) v.findViewById(R.id.from);
                vh.toView = (TextView) v.findViewById(R.id.to);

                return vh;
            }

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_route, parent, false);

            RouteViewHolder vh = new RouteViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (position == 0) {
                String from = getString(R.string.from_xxx, mFromLocalityName);
                String to = getString(R.string.to_xxx, mToLocalityName);

                ((ViewHolder) viewHolder).fromView.setText(from);
                ((ViewHolder) viewHolder).toView.setText(to);
            } else {
                ((RouteViewHolder) viewHolder).numberView.setText(String.valueOf(position));
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return 10;
        }
    }
}