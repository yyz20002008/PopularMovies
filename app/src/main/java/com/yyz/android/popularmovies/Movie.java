package com.yyz.android.popularmovies;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by James Yang on 7/14/2015.
 * Model for Movie, convenient for code re-use
 */
public class Movie {
    public static final String FLAG_MOVIE = "com.yyz.android.popularmovies.FLAG_MOVIE";
    public static final String ID="id";
    public static final String TITLE="original_title";
    public static final String OVERVIEW="overview";
    public static final String RELEASE_DATE="release_date";
    public static final String POSTER_PATH="poster_path";
    public static final String BACKDROP_PATH="backdrop_path";
    public static final String POPULARITY="popularity";
    public static final String VOTE_AVERAGE="vote_average";
    public static final String VOTE_COUNT="vote_count";

    public final int id;
    public final String title;
    public final String overview;
    public final String release_date;
    public final String poster_path;
    public final String backdrop_path;
    public final double popularity;
    public final double vote_average;
    public final int vote_count;

    public Movie(int id, String title, String overview, String release_date,String poster_path,
            String backdrop_path, double popularity,double vote_average,int vote_count){
        this.id=id;
        this.title=title;
        this.overview=overview;
        this.release_date=release_date;
        this.poster_path=poster_path;
        this.backdrop_path=backdrop_path;
        this.popularity=popularity;
        this.vote_average=vote_average;
        this.vote_count=vote_count;
    }

    public Movie (Bundle bundle){
        this(
                bundle.getInt(ID),
                bundle.getString(TITLE),
                bundle.getString(OVERVIEW),
                bundle.getString(RELEASE_DATE),
                bundle.getString(POSTER_PATH),
                bundle.getString(BACKDROP_PATH),
                bundle.getDouble(POPULARITY),
                bundle.getDouble(VOTE_AVERAGE),
                bundle.getInt(VOTE_COUNT)
        );
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putInt(ID, id);
        bundle.putString(TITLE, title);
        bundle.putString(OVERVIEW, overview);
        bundle.putString(RELEASE_DATE, release_date);
        bundle.putString(POSTER_PATH, poster_path);
        bundle.putString(BACKDROP_PATH, backdrop_path);
        bundle.putDouble(POPULARITY,popularity);
        bundle.putDouble(VOTE_AVERAGE, vote_average);
        bundle.putInt(VOTE_COUNT, vote_count);

        return bundle;
    }


    public static Movie fromJson(JSONObject jsonObject) throws JSONException {
        return new Movie(
                jsonObject.getInt(ID),
                jsonObject.getString(TITLE),
                jsonObject.getString(OVERVIEW),
                jsonObject.getString(RELEASE_DATE),
                jsonObject.getString(POSTER_PATH),
                jsonObject.getString(BACKDROP_PATH),
                jsonObject.getDouble(POPULARITY),
                jsonObject.getDouble(VOTE_AVERAGE),
                jsonObject.getInt(VOTE_COUNT)

        );
    }

    public String getRating(){
        return vote_average+"/10.0";
    }


}
