package com.smp.funwithmusic.apiclient;

import static com.smp.funwithmusic.global.Constants.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smp.funwithmusic.global.URLParamEncoder;

public class SongKickClient
{
	private final static String BASE_URL = "http://api.songkick.com/api/3.0/";
	
	private final static String ARTIST_ID_URL = "search/artist.json?";
	
	private final static String ARTIST_EVENTS_URL = "calendar.json?";
	
	public static void getId(RequestQueue queue, Object tag, String artist,
			Response.Listener<JSONObject> responseHandler, Response.ErrorListener errorHandler)
	{
		String params = "query=" + URLParamEncoder.encode("{" + artist + "}")
				.replace(ESCAPED_SPACE, SONGKICK_TERMS_CONNECTOR)
				+ "&apikey=" + API_KEY_SONGKICK;

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
				BASE_URL + ARTIST_ID_URL + params, null, responseHandler, errorHandler);
		
		jsObjRequest.setTag(tag);
		queue.add(jsObjRequest);
	}
	
	public static String parseId(JSONObject obj)
	{
		String result = null;
		try
		{
			result = obj.getJSONObject("resultsPage")
					.getJSONObject("results")
					.getJSONArray("artist")
					.getJSONObject(0)
					.getString("id");
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("ID", result);
		return result;
	}
	
}
