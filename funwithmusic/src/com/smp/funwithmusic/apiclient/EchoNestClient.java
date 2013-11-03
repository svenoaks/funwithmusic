package com.smp.funwithmusic.apiclient;

import static com.smp.funwithmusic.utilities.Constants.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smp.funwithmusic.dataobjects.Biography;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.utilities.URLParamEncoder;

public class EchoNestClient
{
	private final static String BASE_URL = "http://developer.echonest.com/api/v4/";
	private final static String ARTIST_URL = "artist/";

	private final static String IDENTIFY_URL = "song/identify?";
	private final static String ECHOPRINT_VERSION = "4.12";

	// private final static String NON_ASCII = "[^\\p{ASCII}]";

	public enum echoNestRequest
	{
		BIOGRAPHIES, BLOGS, IMAGES, NEWS, REVIEWS, TWITTER, URLS, VIDEOS, SONGS
	};

	private static AsyncHttpClient client = new AsyncHttpClient();
	static
	{
		client.setMaxRetriesAndTimeout(HTTP_RETRIES, HTTP_TIMEOUT);
	}

	public static void getArtistInfo(String artist, echoNestRequest request, JsonHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		// Log.d("artist", Normalizer.normalize(artist,
		// Normalizer.Form.NFD).replaceAll(NON_ASCII, "") + " " + artist);
		// String nString = Normalizer.normalize(artist,
		// Normalizer.Form.NFD).replaceAll(NON_ASCII, "");
		params.put("api_key", API_KEY_ECHO_NEST);
		params.put("name", URLParamEncoder.encode(artist)
				.replace(ESCAPED_SPACE, ECHO_NEST_TERMS_CONNECTOR));
		params.put("format", "json");
		params.put("results", "100");
		Locale locale = Locale.getDefault();
		// Log.d("Images",BASE_URL + ARTIST_URL +
		// request.toString().toLowerCase(locale) + "?" );
		client.get(BASE_URL + ARTIST_URL + request.toString().toLowerCase(locale) + "?", params, responseHandler);
	}

	public static List<Biography> parseBiographies(JSONObject json)
	{
		List<Biography> result = new ArrayList<Biography>();

		try
		{
			JSONObject response = json.getJSONObject("response");
			JSONArray bios = response.getJSONArray("biographies");

			for (int i = 0; i < bios.length(); ++i)
			{
				JSONObject bioJ = bios.getJSONObject(i);

				String site = bioJ.getString("site");
				String url = bioJ.getString("url");

				String text = bioJ.getString("text");

				// if (!site.equals("last.fm") && !text.equals(""))
				// {
				if (!text.equals(""))
				{
					int tl = text.length() > 300 ? 300 : text.length();
					text = text.substring(0, tl);
				}

				// }
				if (text.length() >= 3 &&
						!text.substring(text.length() - 3).equals("..."))
				{
					text += "...";
				}

				if (site.equals("wikipedia") || (site.equals("last.fm")
						|| site.equals("itunes")) || site.equals("amazon"))

				{

					Biography bio = new Biography.Builder()
							.withText(text)
							.withSite(site)
							.withUrl(url)
							.build();

					result.add(bio);
				}
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static List<String> parseImages(JSONObject json)
	{
		// Log.d("Images", "JSON:   " + json.toString());
		List<String> urls = new ArrayList<String>();
		try
		{
			JSONObject response = json.getJSONObject("response");
			JSONArray images = response.getJSONArray("images");

			for (int i = 0; i < images.length(); ++i)
			{
				// Log.d("Images", "In parseImages  " +
				// images.getJSONObject(i).getString("url"));
				urls.add(images.getJSONObject(i).getString("url"));

			}

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urls;
	}

	public static void getIdentify(String code, JsonHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("api_key", API_KEY_ECHO_NEST);
		params.put("version", ECHOPRINT_VERSION);
		params.put("code", code);

		// Log.d("Lyrics", AsyncHttpClient.getUrlWithQueryString(BASE_URL,
		// params));

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
