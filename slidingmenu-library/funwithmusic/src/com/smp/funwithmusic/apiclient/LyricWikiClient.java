package com.smp.funwithmusic.apiclient;

import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smp.funwithmusic.utilities.URLParamEncoder;

import static com.smp.funwithmusic.utilities.Constants.*;

public class LyricWikiClient
{
	public static final String BASE_URL = "http://lyrics.wikia.com/api.php?";
	
	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String title, String artist, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		
		params.put("func", "getSong");
		params.put("song", URLParamEncoder.encode(title)
				.replace(ESCAPED_SPACE, LYRICS_WIKI_TERMS_CONNECTOR));
		params.put("artist", URLParamEncoder.encode(artist)
				.replace(ESCAPED_SPACE, LYRICS_WIKI_TERMS_CONNECTOR));
		params.put("fmt", "json");
		//Log.d("Lyrics", AsyncHttpClient.getUrlWithQueryString(BASE_URL, params));
		
		client.get(BASE_URL, params, responseHandler);
	}

	public static String getShortLyric(JSONObject json)
	{
		String result = null;
		
		result = json.optString("lyrics");
		//Log.d("LYRICS", "JKDJKKJ");
		
		return result;
	}
	
	public static String getFullLyricsUrl(JSONObject json)
	{
		String result = null;
		
		result = json.optString("url");
		
		return result;
	}
}
