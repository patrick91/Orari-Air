package it.patrick91.orariair.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import it.patrick91.orariair.R;
import it.patrick91.orariair.fragments.RoutesFragment;

/**
 * Created by patrick on 28/12/14.
 */
public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {
    private static final int VIEW_TYPE_ROUTE = 0;
    private final Context mContext;
    private Cursor mCursor;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView numberView;
        public final TextView timeView;
        public final TextView hourView;

        public ViewHolder(View view) {
            super(view);

            numberView = (TextView) view.findViewById(R.id.number);
            timeView = (TextView) view.findViewById(R.id.time);
            hourView = (TextView) view.findViewById(R.id.hour);
        }
    }

    public RoutesAdapter(Context context, Cursor c) {
        mContext = context;
        mCursor = c;
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);

        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor cursor) {
        if (mCursor == cursor) {
            return null;
        }

        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (cursor != null) {
            this.notifyDataSetChanged();
        }

        return oldCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String duration = mCursor.getString(RoutesFragment.COL_DURATION);
        String departure = mCursor.getString(RoutesFragment.COL_START_TIME);
        String arrival = mCursor.getString(RoutesFragment.COL_END_TIME);

        String hour = mContext.getString(R.string.departure_arrival, departure, arrival);

        holder.numberView.setText(String.valueOf(mCursor.getPosition()));
        holder.hourView.setText(hour);
        holder.timeView.setText(duration);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ROUTE;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }
}
