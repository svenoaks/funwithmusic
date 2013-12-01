package com.smp.funwithmusic.asynctask;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import static com.smp.funwithmusic.global.Constants.*;

import android.os.AsyncTask;
import android.widget.ImageView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.*;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;


public class YouTubeQueryAsyncTask extends AsyncTask<String, Void, List<SearchResult>>
{
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final long NUMBER_OF_VIDEOS_RETURNED = 25;
	private static final YouTube youtube;
	
	static
	{
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer()
		{
			public void initialize(HttpRequest request) throws IOException
			{
			}
		}).setApplicationName("Music Flow").build();
	}

	private OnYoutubeSearchResults listener;
	
	public YouTubeQueryAsyncTask(OnYoutubeSearchResults listener)
	{
		this.listener = listener;
	}

	protected List<SearchResult> doInBackground(String... args)
	{
		List<SearchResult> searchResultList = null;
		try
		{
			String queryTerm = args[0];
			YouTube.Search.List search = youtube.search().list("id,snippet");

			String apiKey = API_KEY_BROWSER_YOUTUBE;
			search.setKey(apiKey);
			search.setQ(queryTerm);
			
			search.setType("video");
			
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/high/url)");
			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			SearchListResponse searchResponse = search.execute();

			searchResultList = searchResponse.getItems();
		}
		catch (GoogleJsonResponseException e)
		{
			System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
					+ e.getDetails().getMessage());
		}
		catch (IOException e)
		{
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		}

		return searchResultList;
	}

	protected void onPostExecute(List<SearchResult> result)
	{
		if (listener != null) listener.onSearchResultsReceived(result);
	}

	public void releaseReference()
	{
		listener = null;
	}
}
