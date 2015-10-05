package com.yyz.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    SharedPreferences sharedPref;
    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        setContentView(R.layout.activity_main);

//        if (findViewById(R.id.fragment_detail) != null) {
//            mTwoPane = true;
//
//        } else {
//            mTwoPane = false;
//        }
//        System.out.println("LLLLLLLLLLLL:"+mTwoPane);
    }

//    @Override
//    public void onItemSelected(Movie movie) {
//        if (mTwoPane) {
//            Bundle arguments = new Bundle();
//            arguments.putBundle(Movie.FLAG_MOVIE, movie.toBundle());
//
//            DetailActivityFragment fragment = new DetailActivityFragment();
//            fragment.setArguments(arguments);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_detail, fragment, Movie.FLAG_MOVIE)
//                    .commit();
//        } else {
//            Intent intent = new Intent(this, DetailActivity.class)
//                    .putExtra(Movie.FLAG_MOVIE, movie.toBundle());
//            startActivity(intent);
//        }
//    }

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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }





}
