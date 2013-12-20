package com.smp.funwithmusic.apiclient;

import static com.smp.funwithmusic.global.Constants.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smp.funwithmusic.dataobjects.Biography;
import com.smp.funwithmusic.dataobjects.NewsReview;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.global.URLParamEncoder;

public class EchoNestClient
{
	private final static String BASE_URL = "http://developer.echonest.com/api/v4/";
	private final static String ARTIST_URL = "artist/";

	private final static String IDENTIFY_URL = "song/identify?";
	private final static String ECHOPRINT_VERSION = "4.12";

	private final static String RAW_WIKIPEDIA = "Wikipedia";
	private final static String CORRECT_WIKIPEDIA = "WikipediA";

	private final static String RAW_LAST_FM = "last.fm";
	private final static String CORRECT_LAST_FM = "last.fm";

	private final static String RAW_AMAZON = "amazon";
	private final static String CORRECT_AMAZON = "Amazon";

	private final static String RAW_ITUNES = "itunes";
	private final static String CORRECT_ITUNES = "iTunes";
	
	private final static int BIOS_MAX_CHARS = 350;
	private final static int NEWSREVIEWS_MAX_CHARS = 200;

	private static Pattern pattern = Pattern.compile("</?[sS][pP][aA][nN]>");

	public enum echoNestRequest
	{
		BIOGRAPHIES, BLOGS, IMAGES, NEWS, REVIEWS, TWITTER, URLS, VIDEOS, SONGS
	};

	public static void getArtistInfo(RequestQueue queue, String tag, String artist, echoNestRequest request,
			Response.Listener<JSONObject> responseHandler, Response.ErrorListener errorHandler)
	{
		String params = "api_key=" + API_KEY_ECHONEST
				+ "&name=" + URLParamEncoder.encode(artist)
						.replace(ESCAPED_SPACE, ECHO_NEST_TERMS_CONNECTOR)
				+ "&format=json"
				+ "&results=100";

		if (request == echoNestRequest.NEWS)
			params += "&high_relevance=true";

		Locale locale = Locale.getDefault();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
				BASE_URL + ARTIST_URL + request.toString().toLowerCase(locale) + "?"
						+ params, null, responseHandler, errorHandler);

		// jsObjRequest.setShouldCache(false);
		jsObjRequest.setTag(tag);

		queue.add(jsObjRequest);
	}

	public static List<NewsReview> parseNewsReviews(JSONObject json, echoNestRequest request)
	{
		List<NewsReview> result = new ArrayList<NewsReview>();
		String toGet = null;
		switch (request)
		{
			case NEWS:
				toGet = "news";
				break;
			case REVIEWS:
				toGet = "reviews";
				break;
			default:
				throw new IllegalArgumentException("Request not supported");
		}
		try
		{
			JSONObject response = json.getJSONObject("response");
			JSONArray reviews = response.getJSONArray(toGet);

			for (int i = 0; i < reviews.length(); ++i)
			{
				JSONObject reviewJ = reviews.getJSONObject(i);

				String name = reviewJ.optString("name");
				String url = reviewJ.optString("url");
				String summary = reviewJ.optString("summary");
				String date = reviewJ.optString("date_reviewed");
				String image_url = reviewJ.optString("image_url");
				String release = reviewJ.optString("release");
				
				if (name != null)
				{
					name = pattern.matcher(name).replaceAll("");
					name = Html.fromHtml((String) name).toString();
				}
				if (summary != null)
				{
					summary = processText(summary, NEWSREVIEWS_MAX_CHARS);
					summary = Html.fromHtml((String) summary).toString();
				}
				

				if (!url.contains("music.aol.com")
						&& !url.contains("splendidzine.com"))
				{
					NewsReview review = new NewsReview.Builder()
							.withName(name)
							.withUrl(url)
							.withSummary(summary)
							.withDate(date)
							.withImage_url(image_url)
							.withRelease(release)
							.build();

					result.add(review);
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return result;
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

				text = processText(text, BIOS_MAX_CHARS);
				site = processSite(site);

				if (site.equals(CORRECT_WIKIPEDIA) || (site.equals(CORRECT_LAST_FM)
						|| site.equals(CORRECT_ITUNES)) || site.equals(CORRECT_AMAZON))

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

	private static String processSite(String site)
	{
		if (site.equals(RAW_WIKIPEDIA))
			site = CORRECT_WIKIPEDIA;
		else if (site.equals(RAW_LAST_FM))
			site = CORRECT_LAST_FM;
		else if (site.equals(RAW_ITUNES))
			site = CORRECT_ITUNES;
		else if (site.equals(RAW_AMAZON))
			site = CORRECT_AMAZON;

		return site;
	}

	private static String processText(String text, int maxChars)
	{
		final int LONG_ENOUGH = 3;
		final String ELLIPSES = "...";

		if (!text.equals(""))
		{
			text = pattern.matcher(text)
					.replaceAll("");
			int tl = text.length() > maxChars ? maxChars : text.length();
			text = text.substring(0, tl);
		}

		if (text.length() >= LONG_ENOUGH &&
				!text.substring(text.length() - LONG_ENOUGH).equals(ELLIPSES))
		{
			int i;
			for (i = text.length() - 1; i > 0; --i)
			{
				String chars = text.substring(i - 1, i + 1);
				if (chars.matches("\\S\\s"))
				{
					break;
				}
			}
			text = text.substring(0, i);
			text += ELLIPSES;
		}
		return text;
	}

	public static List<String> parseImages(JSONObject json)
	{
		List<String> urls = new ArrayList<String>();
		try
		{
			JSONObject response = json.getJSONObject("response");
			JSONArray images = response.getJSONArray("images");

			for (int i = 0; i < images.length(); ++i)
			{
				urls.add(images.getJSONObject(i).getString("url"));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return urls;
	}
}
