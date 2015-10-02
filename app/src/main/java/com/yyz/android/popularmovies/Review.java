package com.yyz.android.popularmovies;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by James Yang on 9/13/2015.
 */
public class Review {
    public static final String FLAG_Review= "com.yyz.android.popularmovies.FLAG_Review";
    public static final String ID="id";
    public static final String AUTHOR="author";
    public static final String CONTENT="content";
    public static final String URL="url";



    public final String id;
    public final String author;
    public final String content;
    public final String url;

    public Review(String id, String author, String content, String url){
        this.id=id;
        this.author=author;
        this.content=content;
        this.url=url;

    }

    public Review (Bundle bundle){
        this(
                bundle.getString(ID),
                bundle.getString(AUTHOR),
                bundle.getString(CONTENT),
                bundle.getString(URL)

        );
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putString(ID, id);
        bundle.putString(AUTHOR, author);
        bundle.putString(CONTENT, content);
        bundle.putString(URL, url);

        return bundle;
    }


    public static Review fromJson(JSONObject jsonObject) throws JSONException {
        return new Review(
                jsonObject.getString(ID),
                jsonObject.getString(AUTHOR),
                jsonObject.getString(CONTENT),
                jsonObject.getString(URL)

        );
    }

}
