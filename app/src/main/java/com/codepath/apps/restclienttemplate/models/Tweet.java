package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {

    public String body;
    public String createdAt;
    public User user;
    public String id;
    public String pic_url;
    public boolean isFavorited;
    public boolean isRetweeted;
    public int favoriteCount;
    public String reply;
    public int replyCount;


    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("retweeted_status")) {
            return null;
        }

        Tweet tweet = new Tweet();
        if(jsonObject.has("full_text")) {
            // extended must be true
            tweet.body = jsonObject.getString("full_text");
        } else {
            // extended must be false
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getString("id_str");
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.isRetweeted = jsonObject.getBoolean("retweeted");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        // tweet.replyCount = jsonObject.getInt("reply_count");

        if (jsonObject.getJSONObject("entities").has("media")) {
            tweet.pic_url = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
        }
        else {
            tweet.pic_url = "none";
        }


        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {

        List<Tweet> tweets = new ArrayList<>();
        int i;

        for (i = 0; i < jsonArray.length(); i++) {
            Tweet newTweet = fromJson(jsonArray.getJSONObject(i));
            if (newTweet != null)  { // skip retweets
                tweets.add(newTweet);
            }
        }

        return tweets;
    }
}
