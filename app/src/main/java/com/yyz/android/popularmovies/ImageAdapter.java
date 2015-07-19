package com.yyz.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by James Yang on 7/12/2015.
 */
public class ImageAdapter extends BaseAdapter{
    private Context mContext;
    private final ArrayList<Movie> mMovies;
    private final String BASE_URL = "http://image.tmdb.org/t/p/";
    private final int mHeight;
    private final int mWidth;


    public ImageAdapter(Context c) {

        mContext = c;
        mMovies = new ArrayList<Movie>();
        mHeight= Math.round(mContext.getResources().getDimension(R.dimen.poster_height));
        mWidth = Math.round(mContext.getResources().getDimension(R.dimen.poster_width));
    }

    public void addAll(Collection<Movie> xs) {

        mMovies.addAll(xs);
        notifyDataSetChanged();
    }

    public int getCount() {
        return mMovies.size();
    }//

    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mWidth,mHeight));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }



        Uri posterUri =Uri.parse(BASE_URL).buildUpon()
                .appendPath(mContext.getString(R.string.api_poster_default_size))
                .appendEncodedPath(mMovies.get(position).poster_path)
                .build();

        Picasso.with(mContext)
                .load(posterUri)
                .into(imageView);

        return imageView;
    }

//    // references to our images
//    private Integer[] mThumbIds = {
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7,
//            R.drawable.sample_0, R.drawable.sample_1,
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7,
//            R.drawable.sample_0, R.drawable.sample_1,
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7
//    };
}
