package com.smp.funwithmusic.activities;

import static com.smp.funwithmusic.global.Constants.*;
import static com.smp.funwithmusic.global.UtilityMethods.*;

import java.util.List;
import java.util.Locale;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.Card.CardMenuListener;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;
import com.afollestad.silk.adapters.SilkAdapter.ViewHolder;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.adapters.SongCardAdapter;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.dataobjects.SongCard;
import com.smp.funwithmusic.global.GlobalRequest;
import com.smp.funwithmusic.services.IdentifyMusicService;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.AbsListView.RecyclerListener;

public class FlowActivity extends Activity implements CardMenuListener<Card>
{
	@Override
	protected void onNewIntent(Intent intent)
	{
		// this is a new Intent from Music Id in Artist Activity, so
		// we want to scroll to the bottom of the list.
		if (intent.getBooleanExtra(EXTRA_SHOULD_SCROLL, false))
			shouldScrollToBottom = true;
		setIntent(intent);
		super.onNewIntent(intent);
	}

	private List<Song> songs;
	private IntentFilter filter;
	private UpdateActivityReceiver receiver;
	private SongCardAdapter<SongCard> cardsAdapter;
	private CardListView cardsList;
	private String lastArtist;
	private View idDialog;
	private View welcomeScreen;
	private boolean shouldScrollToBottom;
	private int listPosition, listPositionTop = INVALID;

	private class UpdateActivityReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(ACTION_REMOVE_IDENTIFY))
			{
				progressStopSpin(idDialog);
				viewGone(idDialog);
			}
			else if (intent.getAction().equals(ACTION_ADD_SONG))
			{
				addCardsFromList();
				if (intent.getBooleanExtra(EXTRA_FROM_ID, false))
					scrollToPositionInList(cardsAdapter.getCount() - 1);
			}
		}
	}

	private void resetArtist()
	{
		lastArtist = null;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		saveSongs();
		resetArtist();
		Log.d("PAUSE", "PAUSED");
		cardsAdapter.releaseListenerReferences();
		GlobalRequest.getInstance(this).getRequestQueue().cancelAll(TAG_VOLLEY);

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
		// Log.d("PAUSE", "reSUMED");
		addCardsFromList();
		if (shouldScrollToBottom)
		{
			scrollToPositionInList(cardsAdapter.getCount() - 1);
			shouldScrollToBottom = false;
		}
		else if (listPosition != INVALID && listPositionTop != INVALID)
		{
			cardsList.setSelectionFromTop(listPosition, listPositionTop);
			listPosition = listPositionTop = INVALID;
		}

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

		if (isMyServiceRunning(this, IdentifyMusicService.class))
		{
			viewVisible(idDialog);
			progressSpin(idDialog);
		}
		else
		{
			viewGone(idDialog);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		int index = cardsList.getFirstVisiblePosition();
		View v = cardsList.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();

		outState.putInt(BUNDLE_LIST_POSITION, index);
		outState.putInt(BUNDLE_LIST_POSITION_TOP, top);
	}

	// Scrolls the view so that last item is at the bottom of the screen.
	//
	private void scrollToPositionInList(final int position)
	{
		cardsList.post(new Runnable()
		{
			@Override
			public void run()
			{
				// Select the last row so it will scroll into view
				cardsList.setSelection(position);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flow);
		cardsAdapter = new SongCardAdapter<SongCard>(this,
				GlobalRequest.getInstance(this).getRequestQueue());
		cardsAdapter.setAccentColorRes(R.color.holo_blue_dark);
		cardsAdapter.setPopupMenu(R.menu.card_popup, this); // the popup menu

		idDialog = findViewById(R.id.progress);
		welcomeScreen = findViewById(R.id.welcome_screen);

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

				String logKey = getResources().getString(R.string.pref_key_card_click);
				SharedPreferences pref = getPref(FlowActivity.this);
				
				String value = pref.getString(logKey, "error");

				CardClickAction action = CardClickAction.valueOf(value);
				
				switch (action)
				{
					case LYRICS:
						doLyrics(song);
						break;
					case YOUTUBE:
						doYouTube(song);
						break;
					case CLIPBOARD:
						doClipboard(song);
						break;
					default:
						throw new RuntimeException("not a valid click action");
				}
			}
		});

		if (savedInstanceState != null)
		{
			listPosition = savedInstanceState.getInt(BUNDLE_LIST_POSITION);
			listPositionTop = savedInstanceState.getInt(BUNDLE_LIST_POSITION_TOP);
		}
		
		filter = new IntentFilter();
		filter.addAction(ACTION_ADD_SONG);
		filter.addAction(ACTION_REMOVE_IDENTIFY);
		filter.addCategory(Intent.CATEGORY_DEFAULT);

		receiver = new UpdateActivityReceiver();
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	protected void doClipboard(Song song)
	{
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("MUSIC_FLOW", song.getArtist() + SPACE
				+ song.getTitle());
		clipboard.setPrimaryClip(clip);
		Toast.makeText(this, "Artist and Song info saved to clipboard.", Toast.LENGTH_SHORT).show();
	}

	protected void doYouTube(Song song)
	{
		Intent intent = new Intent(this, YouTubeSelectionActivity.class);
		intent.putExtra(EXTRA_YOUTUBE_SEARCH_TERMS
				, song.getArtist() + SPACE + song.getTitle());
		startActivity(intent);	
	}

	protected void doLyrics(Song song)
	{
		if (song.hasLyrics())
		{
			Uri uri = Uri.parse(song.getFullLyricsUrl());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
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
		if (cardsAdapter.getCount() == 0)
		{
			viewVisible(welcomeScreen);
		}
		else
		{
			viewGone(welcomeScreen);
		}
	}

	// Adds to same stack if the artist is the same.

	private void addCard(final Song song)
	{
		// lastArtist null check probably not necessary here.
		Locale locale = Locale.getDefault();
		if (cardsAdapter.getCount() != 0 && lastArtist != null
				&& lastArtist.toUpperCase(locale).equals(song.getArtist().toUpperCase(locale)))
		{
			cardsAdapter.add(new SongCard(song));
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

		cardsAdapter.add(new SongCard(song));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.clear:
				doDeleteFlow(this);
				resetArtist();
				addCardsFromList();
				break;
			case R.id.listen:
				if (!isMyServiceRunning(this, IdentifyMusicService.class))
				{
					doListen(getApplicationContext(), idDialog);
				}
				break;
			case R.id.settings:
				Intent intent = new Intent(FlowActivity.this, PrefActivity.class);
				startActivity(intent);
				break;
			case R.id.help:
				Intent help = new Intent(FlowActivity.this, HelpActivity.class);
				startActivity(help);
				break;
			case R.id.licenses:
				Intent licenses = new Intent(FlowActivity.this, LicenseActivity.class);
				startActivity(licenses);
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_flow, menu);
		return true;
	}

	@Override
	public void onMenuItemClick(Card card, MenuItem item)
	{
		SongCard songCard = (SongCard) card;
		Song song = songCard.getSong();
		
		switch (item.getItemId())
		{
			case R.id.lyrics:
				doLyrics(song);
				break;
			case R.id.copy:
				doClipboard(song);
				break;
			case R.id.youtube:
				doYouTube(song);
				break;
		}
	}
}
