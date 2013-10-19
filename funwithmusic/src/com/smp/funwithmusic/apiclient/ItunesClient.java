package com.smp.funwithmusic.apiclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smp.funwithmusic.utilities.URLParamEncoder;

public class ItunesClient
{
	private static final String BASE_URL = "http://itunes.apple.com/search?media=music&attribute=albumTerm&";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String album, JsonHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		params.put("term", URLParamEncoder.encode(album).replace("%20", "+"));
		params.put("media", "music");
		params.put("attribute", "albumTerm");
		params.put("entity", "album");
		params.put("limit", "200");
		client.get(BASE_URL, params, responseHandler);
	}

	public static String getImageUrl(JSONObject json, String artist)
	{
		String result = null;
		JSONArray results;

		try
		{
			results = json.getJSONArray("results");

			for (int i = 0; i < results.length(); ++i)
			{
				JSONObject res = results.getJSONObject(i);
				String candidate = res.optString("artistName");
				Log.i("URL", candidate);
				Log.i("URL", artist);
				if (artist != null && artist.toUpperCase().equals(candidate.toUpperCase()))
				{
					//Log.i("URL", artist);
					result = res.optString("artworkUrl100");
					if (result != null)
						result = res.optString("artworkUrl60");
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return result;
	}
}
