package it.patrick91.orariair.adapters;

import android.content.Context;
import android.database.Cursor;
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
public class RoutesAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_ROUTE = 0;
    private final Context mContext;

    public static class ViewHolder {
        public final TextView numberView;
        public final TextView timeView;
        public final TextView hourView;

        public ViewHolder(View view) {
            numberView = (TextView) view.findViewById(R.id.number);
            timeView = (TextView) view.findViewById(R.id.time);
            hourView = (TextView) view.findViewById(R.id.hour);
        }
    }

    public RoutesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_route, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String duration = cursor.getString(RoutesFragment.COL_DURATION);
        String departure = cursor.getString(RoutesFragment.COL_START_TIME);
        String arrival = cursor.getString(RoutesFragment.COL_END_TIME);

        String hour = mContext.getString(R.string.departure_arrival, departure, arrival);

        viewHolder.numberView.setText(String.valueOf(cursor.getPosition()));
        viewHolder.hourView.setText(hour);
        viewHolder.timeView.setText(duration);
    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ROUTE;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
