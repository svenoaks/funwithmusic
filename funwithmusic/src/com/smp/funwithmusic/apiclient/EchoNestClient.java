package com.smp.funwithmusic.apiclient;

import static com.smp.funwithmusic.utilities.Constants.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.utilities.URLParamEncoder;

public class EchoNestClient
{
	private final static String BASE_URL = "http://developer.echonest.com/api/v4/";
	private final static String IDENTIFY_URL = "song/identify?";
	private final static String ECHOPRINT_VERSION = "4.12";
	
	//public enum echoNestRequest { SONG_IDENTIFY };
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public static void getIdentify(String code, JsonHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		
		params.put("api_key", ECHO_NEST_API_KEY);
		params.put("version", ECHOPRINT_VERSION);
		params.put("code", code);
		
		//Log.d("Lyrics", AsyncHttpClient.getUrlWithQueryString(BASE_URL, params));
		
		client.get(BASE_URL + IDENTIFY_URL, params, responseHandler);
	}
	
	public static Song parseIdentify(JSONObject json)
	{
		Song result = new Song();
		
		try
		{
			JSONObject response = json.getJSONObject("response");
			JSONArray songs = response.getJSONArray("songs");
			JSONObject song = songs.getJSONObject(0);
			result.setArtist(song.getString("artist_name"));
			result.setTitle(song.getString("title"));
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
