package com.codepath.apps.restclienttemplate;

import static com.facebook.stetho.inspector.network.ResponseHandlingInputStream.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;


    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row inflate the layout

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet using viewholder
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // Pass in context + list of tweets

    // For each row inflate the layout

    // Bind values based on the position of the element

    // Define a viewholder **Start here!**
    public class ViewHolder extends RecyclerView.ViewHolder {
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView entity;
        TextView tvFullName;
        TextView tvCreatedAt;
        ImageButton ibFavorite;
        TextView tvFavoriteCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            entity = itemView.findViewById(R.id.entity);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvCreatedAt = itemView.findViewById(R.id.created_at);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);

        }

        @SuppressLint({"CheckResult", "SetTextI18n"})
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Tweet tweet) {
            RequestOptions requestOptionsPI = new RequestOptions();
            RequestOptions requestOptionsIMG = new RequestOptions();

            requestOptionsPI.transform(new CenterCrop(), new RoundedCorners(68));
            requestOptionsIMG.transform(new CenterCrop(), new RoundedCorners(40));
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvFullName.setText(tweet.user.name);
            tvCreatedAt.setText("· " + getRelativeTimeAgo(tweet.createdAt));
            tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
            if (tweet.isFavorited) {
                Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_on);
                ibFavorite.setImageDrawable(newImage);
            }
            else {
                Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_off);
                ibFavorite.setImageDrawable(newImage);
            }


            Glide.with(context).load(tweet.user.profileImageUrl).apply(requestOptionsPI).into(ivProfileImage);
            if (!tweet.pic_url.equals("none")) {
                entity.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.pic_url).apply(requestOptionsIMG).into(entity);
            }
            else {
                entity.setVisibility(View.GONE);
            }

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if not already favorited
                    if (!tweet.isFavorited) {
                        // tell Twitter I favorite this
                        TwitterApp.getRestClient(context).favorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "This should've been favorited!");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i("adapter", "Failure to favorite.");
                            }
                        });
                        // change the drawable
                        Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_on);
                        ibFavorite.setImageDrawable(newImage);
                        // increment the text inside tvFavoriteCount
                        tvFavoriteCount.setText(String.valueOf(++tweet.favoriteCount));
                        tweet.isFavorited = true;


                    }
                    else {
                        // else if already Favorited
                        Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_off);
                        // tell Twitter we want to unfavorite this
                        TwitterApp.getRestClient(context).unfavorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "This should've been unfavorited!");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i("adapter", "Failure to unfavorite");
                            }
                        });

                        // decrement the text inside the tvFavoriteCount
                        tvFavoriteCount.setText(String.valueOf(--tweet.favoriteCount));
                        // change the drawable back to btn_star_big_off
                        ibFavorite.setImageDrawable(newImage);
                        tweet.isFavorited = false;

                    }
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + " m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + " h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + " d";
                }
            } catch (ParseException e) {
                Log.i("error", "getRelativeTimeAgo failed");
                e.printStackTrace();
            }

            return "";
        }
    }
}
