package com.yyz.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;


/**
 * A placeholder fragment containing a simple view.
 * //API Key: 6f033c5f82678f75ff76c30995ba38d6
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    public static final int MAX_PAGES = 10;
    private boolean mIsLoading = false;
    private int mPagesLoaded = 0;
    private ImageAdapter mImages;


    public class FetchMovieTask extends AsyncTask<Integer, Void, Collection<Movie>> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected Collection<Movie> doInBackground(Integer... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            int page = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseJsonStr = null;

            try {
                // Construct the URL for the MovieDB query
                final String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String API_PAGE = "page";
                final String API_KEY = "api_key";
                final String APT_VOTECOUNT="vote_count.gte";//only pull_up vote_counts>90, in case noise movie
                final String API_SORTING = PreferenceManager
                        .getDefaultSharedPreferences(getActivity())
                        .getString(
                                getString(R.string.sorting_key),
                                getString(R.string.sorting_default_value)
                        );

                //URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=6f033c5f82678f75ff76c30995ba38d6");

                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendQueryParameter("sort_by", API_SORTING)
                        .appendQueryParameter(API_PAGE, String.valueOf(page))
                        .appendQueryParameter(APT_VOTECOUNT,getString(R.string.vote_count_default_value))
                        .appendQueryParameter(API_KEY, getString(R.string.api_key))
                        .build();


                Log.d(LOG_TAG, "QUERY URI: " + builtUri.toString());
                URL url = new URL(builtUri.toString());

                // Create the request to themoviedb.org, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                responseJsonStr = buffer.toString();
                //System.out.println(responseJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {

                return getMoviesDataFromJson(responseJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }


        }

        private Collection<Movie> getMoviesDataFromJson(String jsonStr) throws JSONException {
            final String KEY_MOVIES = "results";
            JSONObject json = new JSONObject(jsonStr);
            JSONArray movies = json.getJSONArray(KEY_MOVIES);
            ArrayList<Movie> result = new ArrayList<Movie>();

            for (int i = 0; i < movies.length(); i++) {
                result.add(Movie.fromJson(movies.getJSONObject(i)));

            }

            return result;
        }

        @Override
        protected void onPostExecute(Collection<Movie> ms) {
            if (ms == null) {

                stopLoading();
                return;
            }

            mPagesLoaded++;
            stopLoading();
            mImages.addAll(ms);

        }

    }

    private void startLoading() {
        if (mIsLoading) {
            return;
        }

        if (mPagesLoaded >= MAX_PAGES) {
            return;
        }

        mIsLoading = true;

        new FetchMovieTask().execute(mPagesLoaded + 1);
    }

    private void stopLoading() {
        if (!mIsLoading) {
            return;
        }

        mIsLoading = false;

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        mImages=new ImageAdapter(getActivity());
        gridview.setAdapter(mImages);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ImageAdapter adapter = (ImageAdapter) parent.getAdapter();
                Movie movie = adapter.getItem(position);

                if (movie == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(movie.FLAG_MOVIE, movie.toBundle());
                getActivity().startActivity(intent);
            }
        });

        gridview.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        if (lastInScreen == totalItemCount) {
                           startLoading();
                        }
                    }
                }

        );

        startLoading();
        return rootView;
    }



}
