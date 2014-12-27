package it.patrick91.orariair;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import it.patrick91.orariair.fragments.SearchFragment;
import it.patrick91.orariair.sync.AirSyncAdapter;


public class MainActivity extends ActionBarActivity implements SearchFragment.OnSearchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SearchFragment())
                    .commit();
        }

        AirSyncAdapter.syncImmediately(this);
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
    public void onSearch(long fromId, long toId) {
        Intent searchIntent = new Intent(this, RoutesActivity.class);

        searchIntent.putExtra(RoutesActivity.FROM_ID_KEY, fromId);
        searchIntent.putExtra(RoutesActivity.TO_ID_KEY, toId);

        startActivity(searchIntent);
    }
}
