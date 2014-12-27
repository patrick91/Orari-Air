package it.patrick91.orariair;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import it.patrick91.orariair.fragments.RoutesFragment;


public class RoutesActivity extends ActionBarActivity {
    public static final String FROM_ID_KEY = "from_id";
    public static final String TO_ID_KEY = "to_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        if (savedInstanceState == null) {
            long fromId = getIntent().getLongExtra(FROM_ID_KEY, -1);
            long toId = getIntent().getLongExtra(TO_ID_KEY, -1);

            Bundle arguments = new Bundle();
            arguments.putLong(FROM_ID_KEY, fromId);
            arguments.putLong(TO_ID_KEY, toId);

            RoutesFragment fragment = new RoutesFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_routes, menu);
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
}
