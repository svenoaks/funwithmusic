package com.smp.funwithmusic.adapters;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import static com.smp.funwithmusic.utilities.Constants.*;

import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNCoverArt;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.apiclient.ItunesClient;
import com.smp.funwithmusic.apiclient.LyricWikiClient;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.dataobjects.SongCard;
import com.smp.funwithmusic.receivers.SongReceiver;
import com.smp.funwithmusic.services.IdentifyMusicService;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class SongCardAdapter<T extends SongCard> extends CardAdapter<Card>

{
	private class TextSearchIntoCard implements GNSearchResultReady
	{
		ViewGroup parent;
		Card card;
		Song song;

		TextSearchIntoCard(ViewGroup parent, Card card, Song song)
		{
			this.parent = parent;
			this.card = card;
			this.song = song;
		}

		void doTextSearch(String artist, String album, String title)
		{
			GNOperations.searchByText(this, config, artist, album, null);
		}

		@Override
		public void GNResultReady(GNSearchResult result)
		{
			if (!result.isTextSearchNoMatchStatus())
			{
				// Log.d("Lyrics", "ResultREAdy" + " " + song.getTitle());
				GNSearchResponse response = result.getBestResponse();
				if (response != null)
				{
					GNOperations.fetchByAlbumId(new GNSearchResultReady()
					{

						@Override
						public void GNResultReady(GNSearchResult result)
						{
							GNSearchResponse response = result.getBestResponse();
							if (response != null)
							{
								if (!result.isTextSearchNoMatchStatus())
								{
									String imageUrl = null;
									GNCoverArt art = response.getCoverArt();
									response.getAlbumId();
									if (art != null)
									{
										imageUrl = art.getUrl();
										// Log.d("Lyrics", "art not null");
									}
									// Log.d("Lyrics", "TEST" + imageUrl + " " +
									// response.getAlbumArtist() + " " +
									// response.getAlbumTitle());
									song.setAlbumUrl(imageUrl);
									updateSingleView(parent, card);
								}
							}
						}

					}, config, response.getAlbumId());

				}
			}
		}
	}

	private Context mContext;
	private GNConfig config;
	private RequestQueue queue;
	private final int THUMBNAIL_SIZE_IN_PIXELS;
	private final int THUMBNAIL_SIZE_IN_DP = 56;
	{

	}

	public SongCardAdapter(Context context, RequestQueue queue)
	{
		super(context, R.layout.card_song); // the custom card layout is passed
											// to the super constructor instead

		this.queue = queue; // of every individual card
		mContext = context;
		config = GNConfig.init(API_KEY_GRACENOTE, mContext.getApplicationContext());
		config.setProperty("content.coverArt", "1");
		config.setProperty("content.coverArt.sizePreference", "MEDIUM");
		config.setProperty("content.coverArt.genreCoverArt", "1");

		THUMBNAIL_SIZE_IN_PIXELS = (int) Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				THUMBNAIL_SIZE_IN_DP, context.getResources().getDisplayMetrics()));
		this.queue = queue;
	}

	@Override
	protected boolean onProcessThumbnail(final ImageView icon, final Card card, final ViewGroup parent)
	{
		final Song song = ((SongCard) card).getSong();

		Picasso.with(mContext).load(song.getAlbumUrl())
				.resize(THUMBNAIL_SIZE_IN_PIXELS, THUMBNAIL_SIZE_IN_PIXELS)
				.placeholder(R.drawable.flow)
				.error(R.drawable.flow)
				.into(icon);

		if (!song.hasAlbumUrl() && !song.isCantGetAlbumUrl())
		{

			ItunesClient.get(queue, song.getAlbum(), new Response.Listener<JSONObject>()
			{

				@Override
				public void onResponse(JSONObject obj)
				{
					// TODO Auto-generated method stub
					String url =
							ItunesClient.getImageUrl(obj, song.getArtist());

					song.setAlbumUrl(url);
					// url=null;
					if (url != null)
						updateSingleView(parent, card);
					else
						getCoverFromGraceNote(parent, card, song);
				}

			}, new Response.ErrorListener()
			{
				@Override
				public void onErrorResponse(VolleyError error)
				{
					// Log.d("Lyrics", "Onfailure" + " " + song.getTitle());
					getCoverFromGraceNote(parent, card, song);
				}

			});

			song.setCantGetAlbumUrl(true);
		}

		return true;
	}

	private void getCoverFromGraceNote(ViewGroup parent, Card card, Song song)
	{
		TextSearchIntoCard task = new TextSearchIntoCard(parent, card, song);
		task.doTextSearch(song.getArtist(), song.getAlbum(), song.getTitle());
	}

	public void updateSingleView(ViewGroup parent, Card card)
	{
		ListView list = (ListView) parent;
		int start = list.getFirstVisiblePosition();
		for (int i = start, j = list.getLastVisiblePosition(); i <= j; i++)
			if (card == list.getItemAtPosition(i))
			{
				View view = list.getChildAt(i - start);
				list.getAdapter().getView(i, view, list);
				break;
			}
	}

	@Override
	protected boolean onProcessTitle(TextView title, Card card, int accentColor)
	{
		// Optional, you can modify properties of the title textview here.

		return super.onProcessTitle(title, card, accentColor);
	}

	@Override
	protected boolean onProcessContent(TextView content, Card card)
	{
		// Optional, you can modify properties of the content textview here.
		return super.onProcessContent(content, card);
	}

	// should refactor to use an enum lyricsState within each song.

	protected boolean onProcessLyrics(final TextView lyrics, final Card card, final ViewGroup parent)
	{
		final Song song = ((SongCard) card).getSong();

		if (song.hasLyrics())
		{
			lyrics.setText(song.getShortLyrics());
		}

		else if (!song.isCantGetLyrics())
		{
			song.setLyricsLoading(true);
			song.setCantGetLyrics(true);
			lyrics.setText(LYRICS_LOADING);

			LyricWikiClient.get(queue, song.getTitle(), song.getArtist(), new Response.Listener<String>()
			{
				@Override
				public void onResponse(String text)
				{
					// Log.d("Lyrics", "OnSuccess" + " " + song.getTitle());
					text = text.replace("song = ", "");

					JSONObject obj = null;
					try
					{
						obj = new JSONObject(text);
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
					if (obj != null)
					{
						Locale locale = Locale.getDefault();
						String shortLyrics = LyricWikiClient.getShortLyric(obj);

						if (shortLyrics.toUpperCase(locale).equals(NOT_FOUND.toUpperCase(locale)))
						{
							shortLyrics = NOT_FOUND_WITH_ADD;
							song.setCanAddLyrics(true);
						}
						song.setShortLyrics(shortLyrics);
						song.setFullLyricsUrl(LyricWikiClient.getFullLyricsUrl(obj));
						song.setLyricsLoading(false);
						updateSingleView(parent, card);
					}
				}
			}, new Response.ErrorListener()
			{
				@Override
				public void onErrorResponse(VolleyError error)
				{
					Log.d("Lyrics", "Onfailure" + " " + song.getTitle());
					song.setLyricsLoading(false);
					updateSingleView(parent, card);

				}
			});
		}
		else if (song.isLyricsLoading())
		{
			lyrics.setText(LYRICS_LOADING);
		}
		else if (song.isCanAddLyrics())
		{
			lyrics.setText(NOT_FOUND_WITH_ADD);
		}
		else
		{
			lyrics.setText(COULDNT_FIND_LYRICS);
		}
		return true;
	}

	@Override
	public View onViewCreated(int index, View recycled, Card item, ViewGroup parent)
	{
		TextView lyrics = (TextView) recycled.findViewById(R.id.lyrics);
		if (lyrics != null)
			onProcessLyrics(lyrics, item, parent);

		return super.onViewCreated(index, recycled, item, parent);
	}
}
