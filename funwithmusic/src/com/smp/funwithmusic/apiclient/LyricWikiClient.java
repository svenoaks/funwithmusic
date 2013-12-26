package com.smp.funwithmusic.apiclient;

import java.util.Locale;

import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smp.funwithmusic.global.URLParamEncoder;

import static com.smp.funwithmusic.global.Constants.*;

public class LyricWikiClient
{
	public static final int MAX_LYRICS_LENGTH = 90;
	public static final String BASE_URL = "http://lyrics.wikia.com/api.php?";
	public static final Locale locale;
	static
	{
		locale = Locale.getDefault();
	}

	public static void get(RequestQueue queue, Object tag, String title, String artist,
			Response.Listener<JSONObject> responseHandler, Response.ErrorListener errorHandler)
	{
		String params = "func=getSong"
				+ "&song=" + URLParamEncoder.encode(title.toLowerCase(locale))
						.replace(ESCAPED_SPACE, LYRICS_WIKI_TERMS_CONNECTOR)
				+ "&artist=" + URLParamEncoder.encode(artist.toLowerCase(locale))
						.replace(ESCAPED_SPACE, LYRICS_WIKI_TERMS_CONNECTOR)
				+ "&fmt=realjson";

		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
				BASE_URL + params, null, responseHandler, errorHandler);
		jsonRequest.setTag(tag);
		queue.add(jsonRequest);
	}

	public static String getShortLyric(JSONObject json)
	{
		String lyrics = json.optString("lyrics");
		if (lyrics != null)
		{
			lyrics = EchoNestClient.processText(lyrics, MAX_LYRICS_LENGTH);
			Log.d("LYRICS", lyrics);
			/*
			int tl = lyrics.length() > MAX_LYRICS_LENGTH ? MAX_LYRICS_LENGTH : lyrics.length();
			lyrics = lyrics.substring(0, tl);
			*/
		}
		return lyrics;
	}

	public static String getFullLyricsUrl(JSONObject json)
	{
		return json.optString("url");
	}
}
