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
import com.smp.funwithmusic.apiclient.ItunesClient;
import com.smp.funwithmusic.objects.Song;
import com.smp.funwithmusic.objects.SongCard;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class FlowActivity extends Activity implements CardMenuListener<Card>
{
	SilkImageManager imageManager;
	ArrayList<String> songs;
	private IntentFilter filter;
	private UpdateActivityReceiver receiver;
	CardAdapter<Card> cardsAdapter;
	String lastArtist;

	private class UpdateActivityReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			addCardsFromList();
		}

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		addCardsFromList();
		registerReceiver(receiver, filter);
	}
	
	//need to make old OS friendly
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steve);
		cardsAdapter = new CardAdapter<Card>(this)
				.setAccentColorRes(android.R.color.holo_blue_dark)
				.setPopupMenu(R.menu.card_popup, this); // the popup menu
														// callback is this
														// activity

		CardListView cardsList = (CardListView) findViewById(R.id.cardsList);
		cardsList.setAdapter(cardsAdapter);
		cardsList.setOnCardClickListener(new CardListView.CardClickListener()
		{
			@Override
			public void onCardClick(int index, CardBase card, View view)
			{
				if (index == 0)
				{
					// startActivity(new Intent(MainActivity.this,
					// CustomActivity.class));
				}
			}
		});

		filter = new IntentFilter(SONG_ACTION);
		filter.addCategory(Intent.CATEGORY_DEFAULT);

		receiver = new UpdateActivityReceiver();
		
		imageManager = new SilkImageManager(this);
		imageManager.setCacheDirectory(getCacheDir());
		//imageManager.setFallbackImage(R.drawable.ic_launcher);
		
	}

	private void addCardsFromList()
	{
		ArrayList<Song> songs = getSongList(this);
		if (this.songs == null || this.songs.size() != songs.size())
		{	
			cardsAdapter.clear();
			cardsAdapter.notifyDataSetChanged();
			for (Song song : songs)
			{
				addCard(song);
			}
		}
	}

	// Adds to same stack if the artist is the same.

	private void addCard(final Song song)
	{
		if (lastArtist != null && lastArtist.equals(song.getArtist()))
		{
			cardsAdapter.add(new SongCard(song, this, imageManager));
			return;
		}
		lastArtist = song.getArtist();
		cardsAdapter.add(new CardHeader(song.getArtist())

				.setAction(this, R.string.artist_info, new CardHeader.ActionListener()
				{
					@Override
					public void onClick(CardHeader header)
					{
						Toast.makeText(getApplicationContext(), header.getActionTitle(), Toast.LENGTH_SHORT).show();
					}
				}));
		cardsAdapter.add(new SongCard(song, this, imageManager));
	}

	@Override
	public void onMenuItemClick(Card card, MenuItem item)
	{
		// TODO Auto-generated method stub

	}

}
