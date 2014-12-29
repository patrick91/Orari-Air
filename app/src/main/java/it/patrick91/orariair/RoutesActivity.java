package it.patrick91.orariair;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import it.patrick91.orariair.fragments.RoutesFragment;


public class RoutesActivity extends ActionBarActivity {
    public static final String FROM_ID_KEY = "from_id";
    public static final String TO_ID_KEY = "to_id";
    public static final String DATE_KEY = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            long fromId = getIntent().getLongExtra(FROM_ID_KEY, -1);
            long toId = getIntent().getLongExtra(TO_ID_KEY, -1);
            long date = getIntent().getLongExtra(DATE_KEY, -1);

            Bundle arguments = new Bundle();
            arguments.putLong(FROM_ID_KEY, fromId);
            arguments.putLong(TO_ID_KEY, toId);
            arguments.putLong(DATE_KEY, date);

            RoutesFragment fragment = new RoutesFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }
}
