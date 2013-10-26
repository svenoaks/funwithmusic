package com.smp.funwithmusic.activities;

import static com.smp.funwithmusic.utilities.Constants.*;
import static com.smp.funwithmusic.utilities.UtilityMethods.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.Card.CardMenuListener;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;
import com.afollestad.silk.images.SilkImageManager;
import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.R.id;
import com.smp.funwithmusic.R.layout;
import com.smp.funwithmusic.adapters.SongCardAdapter;
import com.smp.funwithmusic.apiclient.ItunesClient;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.dataobjects.SongCard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class FlowActivity extends Activity implements CardMenuListener<Card>
{
	private List<Song> songs;
	private IntentFilter filter;
	private UpdateActivityReceiver receiver;
	private SongCardAdapter<SongCard> cardsAdapter;
	private CardListView cardsList;
	private String lastArtist;

	private class UpdateActivityReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			addCardsFromList();
		}

	}
	private void reset()
	{
		lastArtist = null;
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(receiver);
		saveSongs();
		reset();
	}

	// reseting the imageUrl and lryics will allow the program to attempt to
	// find them the next time if it couldn't this time.
	// Song object determines if they should be reset internally.
	private void saveSongs()
	{
		for (Song song : songs)
		{
			song.resetImageUrl();
			song.resetLyrics();
		}
		writeObjectToFile(this, SONG_FILE_NAME, songs);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		addCardsFromList();
		scrollToBottomOfList();
		registerReceiver(receiver, filter);
	}

	// Scrolls the view so that last item is at the bottom of the screen.
	//
	private void scrollToBottomOfList()
	{

		cardsList.post(new Runnable()
		{
			@Override
			public void run()
			{
				// Select the last row so it will scroll into view
				cardsList.setSelection(cardsAdapter.getCount() - 1);
			}
		});

	}

	// need to make old OS friendly
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flow);
		cardsAdapter = new SongCardAdapter<SongCard>(this);
		cardsAdapter.setAccentColorRes(android.R.color.holo_blue_dark);
		cardsAdapter.setPopupMenu(R.menu.card_popup, this); // the popup menu
		// callback is this
		// activity

		cardsList = (CardListView) findViewById(R.id.cardsList);
		cardsList.setAdapter(cardsAdapter);
		cardsList.setOnCardClickListener(new CardListView.CardClickListener()
		{
			@SuppressWarnings("rawtypes")
			@Override
			public void onCardClick(int index, CardBase card, View view)
			{

				SongCard songCard = (SongCard) card;
				Song song = songCard.getSong();
				if (song.hasLyrics())
				{

					Intent intent = new Intent(FlowActivity.this, WebActivity.class);
					intent.putExtra(WEB_URL, song.getFullLyricsUrl());
					startActivity(intent);
					/*
					 * Uri uri = Uri.parse(song.getFullLyricsUrl()); Intent
					 * intent = new Intent(Intent.ACTION_VIEW, uri);
					 * startActivity(intent);
					 */
				}

			}
		});

		filter = new IntentFilter(SONG_ACTION);
		filter.addCategory(Intent.CATEGORY_DEFAULT);

		receiver = new UpdateActivityReceiver();

	}

	private void addCardsFromList()
	{
		// songs never is null
		songs = getSongList(this);
		cardsAdapter.clear();
		cardsAdapter.notifyDataSetChanged();
		for (Song song : songs)
		{
			addCard(song);
		}
	}

	// Adds to same stack if the artist is the same.

	private void addCard(final Song song)
	{
		//lastArtist null check probably not necessary here.
		
		if (cardsAdapter.getCount() != 0 && lastArtist != null 
				&& lastArtist.equals(song.getArtist()))
		{
			cardsAdapter.add(new SongCard(song, this));
			return;
		}
		lastArtist = song.getArtist();
		cardsAdapter.add(new CardHeader(song.getArtist())

				.setAction(this, R.string.artist_info, new CardHeader.ActionListener()
				{
					@Override
					public void onClick(CardHeader header)
					{
						Intent intent = new Intent(FlowActivity.this, ArtistActivity.class);
						intent.putExtra(ARTIST_NAME, song.getArtist());
						startActivity(intent);
					}
				}));

		cardsAdapter.add(new SongCard(song, this));
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case R.id.clear:
				deleteFlow();
				addCardsFromList();
				break;
			default:
				return false;
		}
		return true;
	}
	@Override
	public void onMenuItemClick(Card card, MenuItem item)
	{
		

	}
	public void deleteFlow()
	{
		deleteFile(SONG_FILE_NAME);
		reset();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
