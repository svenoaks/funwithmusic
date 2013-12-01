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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.adapters.SongCardAdapter;
import com.smp.funwithmusic.asynctask.YouTubeQueryAsyncTask;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.dataobjects.SongCard;
import com.smp.funwithmusic.global.GlobalRequest;
import com.smp.funwithmusic.services.IdentifyMusicService;
import com.smp.funwithmusic.views.ProgressWheel;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FlowActivity extends Activity implements CardMenuListener<Card>, RecyclerListener
{
	@Override
	protected void onNewIntent(Intent intent)
	{
		// this is a new Intent from Music Id in Artist Activity, so
		// we want to scroll to the bottom of the list.
		shouldScrollToBottom = true;
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
					scrollToBottomOfList();
			}
		}
	}

	/*
	 * @Override public void onScrollStateChanged(AbsListView view, int
	 * scrollState) {
	 * 
	 * switch (scrollState) { case OnScrollListener.SCROLL_STATE_IDLE:
	 * cardsAdapter.setBusy(false);
	 * 
	 * int s = view.getFirstVisiblePosition(); int e =
	 * view.getLastVisiblePosition(); for (int i = s; i <= e; ++i) { Card card =
	 * (Card) view.getItemAtPosition(i); if (card.getTag() == null) { View
	 * thisView = view.getChildAt(i - s); cardsAdapter.getView(i, thisView,
	 * view); } } break; case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	 * cardsAdapter.setBusy(false); break; case
	 * OnScrollListener.SCROLL_STATE_FLING: cardsAdapter.setBusy(true); break; }
	 * 
	 * }
	 * 
	 * @Override public void onScroll(AbsListView view, int firstVisibleItem,
	 * int visibleItemCount, int totalItemCount) {
	 * 
	 * }
	 */
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
		GlobalRequest.getInstance().cancelAll(TAG_VOLLEY);
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
			scrollToBottomOfList();
			shouldScrollToBottom = false;
		}
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

		if (isMyServiceRunning(this, IdentifyMusicService.class))
		{
			viewVisible(idDialog);
		}
		else
		{
			viewGone(idDialog);
		}
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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flow);
		GlobalRequest.init(this);
		cardsAdapter = new SongCardAdapter<SongCard>(this,
				GlobalRequest.getInstance());
		cardsAdapter.setAccentColorRes(R.color.holo_blue_dark);
		cardsAdapter.setPopupMenu(R.menu.card_popup, this); // the popup menu
		// callback is this
		// activity

		TextView progressText = (TextView) findViewById(R.id.progress_text);
		progressText.setText(getResources().getText(R.string.identify));

		idDialog = findViewById(R.id.progress);
		welcomeScreen = findViewById(R.id.welcome_screen);

		cardsList = (CardListView) findViewById(R.id.cardsList);
		cardsList.setAdapter(cardsAdapter);
		// cardsList.setOnScrollListener(this);
		cardsList.setRecyclerListener(this);

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

		filter = new IntentFilter();
		filter.addAction(ACTION_ADD_SONG);
		filter.addAction(ACTION_REMOVE_IDENTIFY);
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
			case R.id.copy:
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("MUSIC_FLOW", song.getArtist() + SPACE
						+ song.getTitle());
				clipboard.setPrimaryClip(clip);
				break;
			case R.id.youtube:
				Intent intent = new Intent(this, YouTubeSelectionActivity.class);
				intent.putExtra(EXTRA_YOUTUBE_SEARCH_TERMS
						,song.getArtist() + SPACE + song.getTitle());
				startActivity(intent);
				break;
		}

	}

	@Override
	public void onMovedToScrapHeap(View view)
	{
		ViewHolder holder = (ViewHolder) view.getTag();
		Picasso.with(this).cancelRequest(holder.icon);
	}

}
