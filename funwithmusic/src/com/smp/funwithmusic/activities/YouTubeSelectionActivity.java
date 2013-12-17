package com.smp.funwithmusic.activities;

import java.util.List;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.api.services.youtube.model.SearchResult;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.adapters.YouTubeSelectionAdapter;
import com.smp.funwithmusic.asynctask.OnYoutubeSearchResults;
import com.smp.funwithmusic.asynctask.YouTubeQueryAsyncTask;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import static com.smp.funwithmusic.global.Constants.*;

public class YouTubeSelectionActivity extends ListActivity implements OnYoutubeSearchResults
{
	private YouTubeQueryAsyncTask task;
	private String query;
	private YouTubeSelectionAdapter adapter;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		SearchResult vid = adapter.getItem(position);
		String vidId = vid.getId().getVideoId();
		Intent intent = YouTubeStandalonePlayer.createVideoIntent
				(this, API_KEY_DEBUG_YOUTUBE, vidId, 0, true, true);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_youtube_search);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		task.releaseReference();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		query = getIntent().getStringExtra(EXTRA_YOUTUBE_SEARCH_TERMS);
		task = new YouTubeQueryAsyncTask(this);
		task.execute(query);
	}

	@Override
	public void onSearchResultsReceived(List<SearchResult> list)
	{
		if (list != null && list.size() > 0)
		{
			adapter = new YouTubeSelectionAdapter(this, list);
			setListAdapter(adapter);
		}
		else
		{
			Toast.makeText(this, "No videos found", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

}
