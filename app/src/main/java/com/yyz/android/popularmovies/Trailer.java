package com.yyz.android.popularmovies;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by James Yang on 9/12/2015.
 */
public class Trailer {
    public static final String FLAG_Trailer = "com.yyz.android.popularmovies.FLAG_Trailer";
    public static final String ID="id";
    public static final String KEY="key";
    public static final String NAME="name";
    public static final String SITE="site";
    public static final String SIZE="size";
    public static final String TYPE="type";


    public final String id;
    public final String key;
    public final String name;
    public final String site;
    public final int size;
    public final String type;

    public Trailer(String id, String key, String name, String site ,int size,
                 String type){
        this.id=id;
        this.key=key;
        this.name=name;
        this.site=site;
        this.size=size;
        this.type=type;

    }

    public Trailer (Bundle bundle){
        this(
                bundle.getString(ID),
                bundle.getString(KEY),
                bundle.getString(NAME),
                bundle.getString(SITE),
                bundle.getInt(SIZE),
                bundle.getString(TYPE)

        );
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putString(ID, id);
        bundle.putString(KEY, key);
        bundle.putString(NAME, name);
        bundle.putString(SITE, site);
        bundle.putInt(SIZE, size);
        bundle.putString(TYPE, type);

        return bundle;
    }


    public static Trailer fromJson(JSONObject jsonObject) throws JSONException {
        return new Trailer(
                jsonObject.getString(ID),
                jsonObject.getString(KEY),
                jsonObject.getString(NAME),
                jsonObject.getString(SITE),
                jsonObject.getInt(SIZE),
                jsonObject.getString(TYPE)

        );
    }



}
