package it.patrick91.orariair;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import it.patrick91.orariair.fragments.RoutesFragment;
import it.patrick91.orariair.fragments.SearchFragment;
import it.patrick91.orariair.sync.AirSyncAdapter;


public class MainActivity extends ActionBarActivity implements SearchFragment.OnSearchListener {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.routes_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            if (savedInstanceState == null) {
                // TODO: show something
            }
        } else {
            mTwoPane = false;
        }

        AirSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearch(long fromId, long toId, long date) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();

            args.putLong(RoutesActivity.FROM_ID_KEY, fromId);
            args.putLong(RoutesActivity.TO_ID_KEY, toId);
            args.putLong(RoutesActivity.DATE_KEY, date);

            RoutesFragment fragment = new RoutesFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.routes_container, fragment)
                    .commit();
        } else {

            Intent searchIntent = new Intent(this, RoutesActivity.class);

            searchIntent.putExtra(RoutesActivity.FROM_ID_KEY, fromId);
            searchIntent.putExtra(RoutesActivity.TO_ID_KEY, toId);
            searchIntent.putExtra(RoutesActivity.DATE_KEY, date);

            startActivity(searchIntent);
        }
    }
}
