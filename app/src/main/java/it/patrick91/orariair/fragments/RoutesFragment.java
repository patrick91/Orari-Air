package it.patrick91.orariair.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.patrick91.orariair.R;

/**
 * Created by patrick on 09/12/14.
 */
public class RoutesFragment extends Fragment {
    public RoutesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);

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

    public static class RoutesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int ITEM_VIEW_TYPE_HEADER = 1;
        private int ITEM_VIEW_TYPE_ROUTE = 2;

        public static class RouteViewHolder extends RecyclerView.ViewHolder {
            public TextView numberView;

            public RouteViewHolder(View itemView) {
                super(itemView);

                numberView = (TextView) itemView.findViewById(R.id.number);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
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