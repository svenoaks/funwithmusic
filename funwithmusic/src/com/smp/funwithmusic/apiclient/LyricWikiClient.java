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
import com.smp.funwithmusic.global.URLParamEncoder;

import static com.smp.funwithmusic.global.Constants.*;

public class LyricWikiClient
{
	public static final int MAX_LYRICS_LENGTH = 90;
	public static final String BASE_URL = "http://lyrics.wikia.com/api.php?";

	public static void get(RequestQueue queue, Object tag, String title, String artist,
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
		stringRequest.setTag(tag);
		queue.add(stringRequest);
	}

	public static String getShortLyric(JSONObject json)
	{	
		String lyrics = json.optString("lyrics");
		if (lyrics != null)
		{
			int tl = lyrics.length() > MAX_LYRICS_LENGTH ? MAX_LYRICS_LENGTH : lyrics.length();
			lyrics = lyrics.substring(0, tl);
		}
		return lyrics;
	}

	public static String getFullLyricsUrl(JSONObject json)
	{
		return json.optString("url");
	}
}
