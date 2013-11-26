package com.smp.funwithmusic.apiclient;

import static com.smp.funwithmusic.global.Constants.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.Performance;
import com.smp.funwithmusic.global.URLParamEncoder;

public class SongKickClient
{
	private final static String BASE_URL = "http://api.songkick.com/api/3.0/";

	private final static String ARTIST_ID_URL = "search/artists.json?";

	private final static String ARTIST_EVENTS_URL = "/calendar.json?";

	private final static String IMAGE_URL = "http://www2.sk-static.com/images/media/profile_images/artists/";

	private final static String IMAGE_URL_SIZE = "/col6";

	public static void getId(RequestQueue queue, Object tag, String artist,
			Response.Listener<JSONObject> responseHandler, Response.ErrorListener errorHandler)
	{
		String params = "query=" + URLParamEncoder.encode(artist)
				.replace(ESCAPED_SPACE, SONGKICK_TERMS_CONNECTOR)
				+ "&apikey=" + API_KEY_SONGKICK;

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
				BASE_URL + ARTIST_ID_URL + params, null, responseHandler, errorHandler);

		jsObjRequest.setTag(tag);
		queue.add(jsObjRequest);
	}

	public static String getImageUrl(String artistId)
	{
		return IMAGE_URL + artistId + IMAGE_URL_SIZE;
	}

	public static void getEvents(RequestQueue queue, Object tag, String artistId,
			Response.Listener<JSONObject> responseHandler, Response.ErrorListener errorHandler)
	{
		String params = "apikey=" + API_KEY_SONGKICK
				+ "&per_page=50";
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
				BASE_URL + "artists/" + artistId + ARTIST_EVENTS_URL + params, null, responseHandler, errorHandler);

		jsObjRequest.setTag(tag);
		Log.d("RESPONSE", jsObjRequest.getUrl());
		queue.add(jsObjRequest);
	}

	public static ArrayList<Event> parseEvents(JSONObject obj)
	{
		ArrayList<Event> result = new ArrayList<Event>();
		Log.d("RESPONSE", obj.toString());
		try
		{
			JSONArray events = obj.getJSONObject("resultsPage")
					.getJSONObject("results")
					.getJSONArray("event");

			for (int i = 0; i < events.length(); ++i)
			{
				JSONObject event = events.getJSONObject(i);

				String type = event.optString("type");
				String mainUri = event.optString("uri");
				String displayName = event.optString("displayName");
				String dateTime = event.optJSONObject("start")
						.optString("datetime");
				String date = event.optJSONObject("start")
						.optString("date");

				JSONArray performances = event.optJSONArray("performance");
				List<Performance> perfs = new ArrayList<Performance>();
				for (int j = 0; j < performances.length(); ++j)
				{
					JSONObject per = performances.optJSONObject(j);
					String perDisplayName = per.optString("displayName");
					int billingIndex = per.optInt("billingIndex");
					String billing = per.optString("billing");

					if (perDisplayName != null)
					{
						Performance p = new Performance.Builder(perDisplayName)
								.billingIndex(billingIndex)
								.billing(billing)
								.build();

						perfs.add(p);
					}
				}

				String location = event.optJSONObject("location")
						.optString("city");
				String venueDisplayName = event.optJSONObject("venue")
						.optString("displayName");
				String venueUri = event.optJSONObject("venue")
						.optString("uri");

				Event e = new Event.Builder(displayName)
						.type(type)
						.mainUri(mainUri)
						.date(date)
						.dateTime(dateTime)
						.performances(perfs)
						.location(location)
						.venueDisplayName(venueDisplayName)
						.venueUri(venueUri)
						.build();

				result.add(e);

			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return result;
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
			e.printStackTrace();
		}
		Log.d("ID", result);
		return result;
	}

}
