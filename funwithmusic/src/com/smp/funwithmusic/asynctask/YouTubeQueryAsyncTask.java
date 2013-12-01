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

//unused
public class YouTubeQueryAsyncTask extends AsyncTask<String, Void, List<SearchResult>>
{
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

	private static YouTube youtube;
	
	static
	{
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer()
		{
			public void initialize(HttpRequest request) throws IOException
			{
			}
		}).setApplicationName("Music Flow").build();
	}
	protected List<SearchResult> doInBackground(String... args)
	{
		List<SearchResult> searchResultList = null;
		try
		{
			String queryTerm = args[0];
			queryTerm = "Birthday";
			YouTube.Search.List search = youtube.search().list("id,snippet");

			String apiKey = API_KEY_BROWSER_YOUTUBE;
			search.setKey(apiKey);
			search.setQ(queryTerm);
			
			search.setType("video");
			
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			SearchListResponse searchResponse = search.execute();

			searchResultList = searchResponse.getItems();
			if (searchResultList != null)
			{
				prettyPrint(searchResultList.iterator(), queryTerm);
			}
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
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		return searchResultList;
	}

	private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query)
	{

		System.out.println("\n=============================================================");
		System.out.println(
				"   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
		System.out.println("=============================================================\n");

		if (!iteratorSearchResults.hasNext())
		{
			System.out.println(" There aren't any results for your query.");
		}

		while (iteratorSearchResults.hasNext())
		{

			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();

			// Double checks the kind is video.
			if (rId.getKind().equals("youtube#video"))
			{
				Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");

				System.out.println(" Video Id" + rId.getVideoId());
				System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
				System.out.println(" Thumbnail: " + thumbnail.getUrl());
				System.out.println("\n-------------------------------------------------------------\n");
			}
		}
	}

	protected void onPostExecute(List<SearchResult> result)
	{

	}
}
