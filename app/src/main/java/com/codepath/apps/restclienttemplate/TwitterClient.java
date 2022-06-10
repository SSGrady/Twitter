package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	public void getHomeTimeline(String maxId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json\n");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		if (maxId != null) {
			params.put("max_id", maxId);
		} // else maxId is null and we get first 25 tweets
		params.put("tweet_mode", "extended");
		params.put("count", 25);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}

	public void publishTweet(String tweetContent, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json\n");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);
		client.post(apiUrl, params,"", handler);
	}

	public void favorite(String tweetId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json\n");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		client.post(apiUrl, params,"", handler);
	}

	public void unfavorite(String tweetId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json\n");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		client.post(apiUrl, params,"", handler);
	}

	public void replyToTweet(String idOfTweetToReplyTo, String tweetContent, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json\n");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", tweetContent); // the body of the tweet
		params.put("in_reply_to_status_id", idOfTweetToReplyTo);
		client.post(apiUrl, params,"", handler);
	}

}
