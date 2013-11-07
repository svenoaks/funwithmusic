package com.smp.funwithmusic.apiclient;

import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smp.funwithmusic.utilities.URLParamEncoder;

import static com.smp.funwithmusic.utilities.Constants.*;

public class LyricWikiClient
{
	public static final String BASE_URL = "http://lyrics.wikia.com/api.php?";

	//private static AsyncHttpClient client = new AsyncHttpClient();

	static
	{
		//client.setMaxRetriesAndTimeout(HTTP_RETRIES, HTTP_TIMEOUT);
		//client.setMaxConnections(100);
	}

	public static void get(RequestQueue queue, String title, String artist, 
			Response.Listener<String> responseHandler, Response.ErrorListener errorHandler)
	{
		String params = "func=getSong&"
				+ "&song=" + URLParamEncoder.encode(title)
				.replace(ESCAPED_SPACE, LYRICS_WIKI_TERMS_CONNECTOR)
				+ "&artist=" + URLParamEncoder.encode(artist)
				.replace(ESCAPED_SPACE, LYRICS_WIKI_TERMS_CONNECTOR)
				+ "&fmt=json";
		
		StringRequest stringRequest = new StringRequest(Request.Method.GET, 
				BASE_URL + params, responseHandler, errorHandler);
		
		queue.add(stringRequest);
		/*
		RequestParams params = new RequestParams();

		params.put("func", "getSong");
		params.put("song", URLParamEncoder.encode(title)
				.replace(ESCAPED_SPACE, LYRICS_WIKI_TERMS_CONNECTOR));
		params.put("artist", URLParamEncoder.encode(artist)
				.replace(ESCAPED_SPACE, LYRICS_WIKI_TERMS_CONNECTOR));
		params.put("fmt", "json");
		*/
		// Log.d("Lyrics", AsyncHttpClient.getUrlWithQueryString(BASE_URL,
		// params));
		//client.get(BASE_URL, params, responseHandler);
	}

	public static String getShortLyric(JSONObject json)
	{
		return json.optString("lyrics");
	}

	public static String getFullLyricsUrl(JSONObject json)
	{
		return json.optString("url");
	}
}
