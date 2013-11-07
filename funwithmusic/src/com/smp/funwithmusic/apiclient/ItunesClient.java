package com.smp.funwithmusic.apiclient;

import java.util.Locale;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smp.funwithmusic.utilities.URLParamEncoder;
import static com.smp.funwithmusic.utilities.Constants.*;

public class ItunesClient
{
	public static final String BASE_URL = "http://itunes.apple.com/search?";

	// private static AsyncHttpClient client = new AsyncHttpClient();
	static
	{
		// queue = Volley.newRequestQueue(this);
		/*
		 * client.setMaxRetriesAndTimeout(HTTP_RETRIES, HTTP_TIMEOUT);
		 * client.setMaxConnections(100);
		 */
	}
	private static Pattern pattern = Pattern.compile("\\s*[(\\[].*[)\\]]\\s*\\z");

	public static void get(RequestQueue queue, String album,
			Response.Listener<JSONObject> responseHandler, Response.ErrorListener errorHandler)
	{
		String params = "term=" + URLParamEncoder.encode(removeAlbumVariations(album))
				.replace(ESCAPED_SPACE, ITUNES_TERMS_CONNECTOR)
				+ "&media=music"
				+ "&attribute=albumTerm"
				+ "&entity=album"
				+ "&limit=200";

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
				BASE_URL + params, null, responseHandler, errorHandler);

		queue.add(jsObjRequest);
		/*
		 * RequestParams params = new RequestParams();
		 * 
		 * params.put("term",
		 * URLParamEncoder.encode(removeAlbumVariations(album))
		 * .replace(ESCAPED_SPACE, ITUNES_TERMS_CONNECTOR).replace(".", ""));
		 * 
		 * params.put("media", "music"); params.put("attribute", "albumTerm");
		 * params.put("entity", "album"); params.put("limit", "200");
		 * 
		 * client.get(BASE_URL, params, responseHandler);
		 */
	}

	// This function will remove variations from the album name. For example,
	// "Violator [Digital Version]" will become "Violator" or Music (US Edition)
	// becomes "Music".

	private static String removeAlbumVariations(String album)
	{
		return pattern.matcher(album)
				.replaceAll("");
	}

	public static String getImageUrl(JSONObject json, String artist)
	{
		String result = null;
		JSONArray results;

		Locale locale = Locale.getDefault();
		try
		{
			results = json.getJSONArray("results");

			for (int i = 0; i < results.length(); ++i)
			{
				JSONObject res = results.getJSONObject(i);
				String candidate = res.optString("artistName");
				// Log.i("URL", candidate);
				// Log.i("URL", artist);
				if (artist != null && artist.toUpperCase(locale)
						.equals(candidate.toUpperCase(locale)))
				{
					// Log.i("URL", artist);
					result = res.optString("artworkUrl100");

					if (result == null)
						result = res.optString("artworkUrl60");

					if (result != null)
						break;
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
