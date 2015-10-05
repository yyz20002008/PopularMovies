package com.yyz.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
 */
public class DetailActivityFragment extends Fragment {


    public DetailActivityFragment() {
    }
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    boolean flag = false;
    Movie movie;
    ArrayList<Trailer> mTrailer = new ArrayList<Trailer>();
    Context context = getActivity();
    SharedPreferences sharedPref;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            movie= new Movie(arguments.getBundle(Movie.FLAG_MOVIE));
        }
        else {

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Movie.FLAG_MOVIE)) {
                movie = new Movie(intent.getBundleExtra(Movie.FLAG_MOVIE));
            }
        }
        System.out.println(movie);
        ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.title);
            ((TextView) rootView.findViewById(R.id.movie_rating)).setText(movie.getRating());
            ((TextView) rootView.findViewById(R.id.movie_overview)).setText(movie.overview);
            ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(movie.release_date);

            // Uri posterUri = movie.buildURI(getString(R.string.api_poster_default_size));

            String BASE_URL = "http://image.tmdb.org/t/p/";

            Uri posterUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(this.getString(R.string.api_poster_default_size))
                    .appendEncodedPath(movie.poster_path)
                    .build();

            Picasso.with(context)
                    .load(posterUri)
                    .into((ImageView) rootView.findViewById(R.id.movie_poster));


            final Button btn = (Button) rootView.findViewById(R.id.btn_favorite);
            btn.setOnClickListener(new View.OnClickListener() {
                //context.getSharedPreferences(getString(R.string.label_movie_favorite), 0).edit().clear().commit();
                @Override
                public void onClick(View arg0) {

                    sharedPref = getActivity().getSharedPreferences(getString(R.string.label_movie_favorite), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor;

                    if (!sharedPref.contains(movie.title)) {
                        editor = sharedPref.edit();
                        editor.putInt(movie.title, movie.id);
                        editor.commit();
                        Toast.makeText(getActivity(),
                                movie.title + " is added to favorite", Toast.LENGTH_SHORT).show();
                        btn.setText("Marked");

                    } else {
                        editor = sharedPref.edit();
                        editor.remove(movie.title);
                        editor.commit();
                        Toast.makeText(getActivity(),
                                movie.title + "is removed", Toast.LENGTH_SHORT).show();
                        btn.setText("Mark as Favorite");

                    }
                    System.out.println(movie.title);//current movie title
                    System.out.println(sharedPref.getAll());
                }

            });

            new FetchTrailerTask().execute(movie.id);
            new FetchReviewTask().execute(movie.id);


       // }
        return rootView;
    }


    //Fetch Trailer
    public class FetchTrailerTask extends AsyncTask<Integer, Void, Collection<Trailer>> {
        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        @Override
        protected Collection<Trailer> doInBackground(Integer... movie_id) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseJsonStr = null;

            try {
                // Construct the URL for the Movie Trailer query
                final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_Mid_URL = "/videos?";
                final String API_KEY = "api_key";
                System.out.println("InSide MovieID= "+movie_id);
                final String M_id=Integer.toString(movie.id);

                System.out.println(API_Mid_URL);

                Uri builtUri = Uri.parse(API_BASE_URL+M_id+API_Mid_URL).buildUpon()
                        .appendQueryParameter(API_KEY, getString(R.string.api_key))
                        .build();


                Log.d(LOG_TAG, "TRAILER URI: " + builtUri.toString());
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
                //Log.e(LOG_TAG, "Error ", e);
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
                        //  Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {

                return getTrailersDataFromJson(responseJsonStr);
            } catch (JSONException e) {
                //Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }

        }
        private Collection<Trailer> getTrailersDataFromJson(String jsonStr) throws JSONException {
            final String KEY_VIDEOS = "results";
            JSONObject json = new JSONObject(jsonStr);
            JSONArray trailers = json.getJSONArray(KEY_VIDEOS);
            ArrayList<Trailer> result = new ArrayList<Trailer>();

            for (int i = 0; i < trailers.length(); i++) {
                result.add(Trailer.fromJson(trailers.getJSONObject(i)));
            }
            return result;
        }
        @Override
        protected void onPostExecute(Collection<Trailer> ms) {
           /* if (ms == null) {
                stopLoading();
                return;
            }

            mPagesLoaded++;
            stopLoading();
            mImages.addAll(ms);
            */
            System.out.println("Inside Trailer size: "+ms.size());
            mTrailer.addAll(ms);

            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.detail_linearlayout);

            for (int i = 0; i < mTrailer.size(); i++)
            {
                TextView tv = new TextView(getActivity());
                tv.setText("Video "+i+": "+mTrailer.get(i).name);
                tv.setId(i + 5);
                tv.setWidth(100);
                tv.setHeight(50);
                tv.setPadding(25, 0, 0, 0);
                tv.setTextAppearance(getActivity(),android.R.style.TextAppearance_Small);
                final String parUrl = mTrailer.get(i).key;
                //System.out.println(parUrl);
                tv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/embed/" + parUrl)));
                    }
                });
                ll.addView(tv);
            }
            TextView line = new TextView(getActivity());
            line.setHeight(1);
            line.setBackgroundResource(R.color.material_blue_grey_900);
            ll.addView(line);

        }

    }

    //Fetch Review
    ArrayList<Review> mReview= new ArrayList<Review>();
    public class FetchReviewTask extends AsyncTask<Integer, Void, Collection<Review>> {
        private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

        @Override
        protected Collection<Review> doInBackground(Integer... movie_id) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseJsonStr = null;

            try {
                // Construct the URL for the Movie Trailer query
                final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_Mid_URL = "/reviews?";
                final String API_KEY = "api_key";
                System.out.println("InSide MovieID= "+movie_id);
                final String M_id=Integer.toString(movie.id);

                System.out.println(API_Mid_URL);

                Uri builtUri = Uri.parse(API_BASE_URL+M_id+API_Mid_URL).buildUpon()
                        .appendQueryParameter(API_KEY, getString(R.string.api_key))
                        .build();


                Log.d(LOG_TAG, "REVIEW URI: " + builtUri.toString());
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

            } catch (IOException e) {
                //Log.e(LOG_TAG, "Error ", e);
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
                        //  Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {

                return getReviewsDataFromJson(responseJsonStr);
            } catch (JSONException e) {
                //Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }

        }
        private Collection<Review> getReviewsDataFromJson(String jsonStr) throws JSONException {
            final String KEY_REVIEWS = "results";
            JSONObject json = new JSONObject(jsonStr);
            JSONArray reviews = json.getJSONArray(KEY_REVIEWS);
            ArrayList<Review> result = new ArrayList<Review>();

            for (int i = 0; i < reviews.length(); i++) {
                result.add(Review.fromJson(reviews.getJSONObject(i)));
            }
            return result;
        }
        @Override
        protected void onPostExecute(Collection<Review> ms) {

            System.out.println("Inside Review size: " + ms.size());
            mReview.addAll(ms);
            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.detail_linearlayout);

            //add Review Label
            LinearLayout.LayoutParams paramsExample = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
            TextView tv_review = new TextView(getActivity());
            tv_review.setLayoutParams(paramsExample);
            tv_review.setText(R.string.label_movie_review);
            tv_review.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
            ll.addView(tv_review);

            for (int i = 0; i < mReview.size(); i++) {
                TextView tv1 = new TextView(getActivity());
                tv1.setText("Author: " + mReview.get(i).author);
                tv1.setId(i + 5);
                tv1.setWidth(100);
                tv1.setHeight(50);
                tv1.setPadding(25, 0, 0, 0);
                tv1.setTextAppearance(getActivity(), android.R.style.TextAppearance_Small);
                ll.addView(tv1);
                TextView tv2 = new TextView(getActivity());
                tv2.setLayoutParams(paramsExample);
                tv2.setText("Comment: "+mReview.get(i).content);
                tv2.setId(i + 5);
                tv2.setPadding(25, 0, 0, 0);
                tv2.setTextAppearance(getActivity(), android.R.style.TextAppearance_Small);
                ll.addView(tv2);
            }

        }

    }
}
