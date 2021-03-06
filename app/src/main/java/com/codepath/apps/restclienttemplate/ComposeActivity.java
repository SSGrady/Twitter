package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final String INTENT_NAME = "tweet";
    public static final String COMPOSE_EMPTY = "Sorry, your tweet cannot be empty!";
    public static final int MAX_TWEET_LENGTH = 140;

    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.i(TAG, "onSuccess to publish tweet.");
            try {
                Tweet tweet = Tweet.fromJson(json.jsonObject);
                Log.e(TAG, "published tweet says " + tweet.body);

                Intent intent = new Intent();
                intent.putExtra(INTENT_NAME, Parcels.wrap(tweet));
                setResult(RESULT_OK, intent);
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            Log.e(TAG, "onFailure to publish tweet.", throwable);
        }
    };

    TwitterClient client;
    EditText etCompose;
    Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);


        // Set up a click listener
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                Toast.makeText(ComposeActivity.this, COMPOSE_EMPTY, Toast.LENGTH_LONG ).show();
                return;
                }

                else if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, COMPOSE_EMPTY, Toast.LENGTH_LONG ).show();
                    return;
                }

                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG ).show();
                // Make an API call to button

                if (getIntent().hasExtra("tweet_to_reply_to")) {
                    Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet_to_reply_to"));
                    String idOfTweetToReplyTo = tweet.id;
                    String screenname = tweet.user.screenName;
                    // handlers are useful to catch errors and define metadata
                    client.replyToTweet(idOfTweetToReplyTo, "@" + screenname + " " + tweetContent, handler);
                } else {
                client.publishTweet(tweetContent, handler);
                }
            }
        });

    }
}