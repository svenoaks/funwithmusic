package com.smp.funwithmusic.adapters;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.NetworkImageView;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import static com.smp.funwithmusic.global.Constants.*;

import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNCoverArt;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.apiclient.ItunesClient;
import com.smp.funwithmusic.apiclient.LyricWikiClient;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.dataobjects.SongCard;
import com.smp.funwithmusic.global.GlobalRequest;
import com.smp.funwithmusic.global.URLParamEncoder;
import com.squareup.picasso.Picasso;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class SongCardAdapter<T extends SongCard> extends CardAdapter<Card>
		implements ResponseReceivedListener
{
	private List<ThumbnailResponseListener> listeners;
	private Context mContext;
	private static GNConfig config;
	private RequestQueue queue;

	// Picasso picasso;

	public SongCardAdapter(Context context, RequestQueue queue)
	{
		super(context, R.layout.list_item_song);

		mContext = context;
		this.queue = queue;
		listeners = new ArrayList<ThumbnailResponseListener>();
		config = GNConfig.init(API_KEY_GRACENOTE, mContext
				.getApplicationContext());
		config.setProperty("content.coverArt", "1");
		config.setProperty("content.coverArt.genreCoverArt", "1");
		config.setProperty("content.coverArt.sizePreference", "SMALL");

		// Picasso.Builder builder = new Picasso.Builder(mContext);
		// picasso = builder.downloader(new OkHttpDownloader(mContext)).build();
		// picasso.setDebugging(true);
	}

	private void registerListener(ThumbnailResponseListener listener)
	{
		listeners.add(listener);
	}

	public void releaseListenerReferences()
	{
		for (ThumbnailResponseListener listener : listeners)
		{
			listener.releaseReferences();
		}
		listeners.clear();
	}

	@Override
	protected boolean onProcessThumbnail(ImageView icon, final Card card,
			final ViewGroup parent)
	{
		
		final Song song = ((SongCard) card).getSong();
		if (icon instanceof NetworkImageView)
		{
			NetworkImageView nIcon = (NetworkImageView) icon;
			//nIcon.setDefaultImageResId(R.drawable.flow);
			nIcon.setErrorImageResId(R.drawable.flow);
			nIcon.setImageUrl(song.getAlbumUrl(), GlobalRequest
					.getInstance(mContext).getImageLoader());
			
		
			 //Log.d("SONG", song.getTitle() + " " + song.getAlbumUrl());
			if (!song.hasAlbumUrl() && !song.isCantGetAlbumUrl())
			{
				ThumbnailListener listen = new ThumbnailListener(this, song,
						card, parent);
				registerListener(listen);
				ItunesClient.get(queue, TAG_VOLLEY, song.getAlbum(), listen, listen);

				song.setCantGetAlbumUrl(true);
			}
		}
		return true;
		
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
		return super.onProcessContent(content, card);
	}

	// should refactor to use an enum lyricsState within each song.

	protected boolean onProcessLyrics(final TextView lyrics, final Card card,
			final ViewGroup parent)
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
			doLyricsListen(parent, card, song);
		}
		else if (song.isLyricsLoading())
		{
			lyrics.setText(LYRICS_LOADING);
		}
		else if (song.isCanSerachLyrics())
		{
			lyrics.setText(SEARCH_LYRICS);
		}
		else
		{
			
			lyrics.setText(COULDNT_FIND_LYRICS);
		}

		return true;

	}

	
	@Override
	public View onViewCreated(int index, View recycled, Card item,
			ViewGroup parent, ViewHolder holder)
	{
		if (holder.content2 == null)
			holder.content2 = (TextView) recycled.findViewById(R.id.lyrics);

		if (holder.content2 != null)
			onProcessLyrics(holder.content2, item, parent);

		return super.onViewCreated(index, recycled, item, parent, holder);
	}

	@Override
	public void beginGracenote(ViewGroup parent, Card card, Song song)
	{
		GracenoteTextResponeListenerStageOne listen =
				new GracenoteTextResponeListenerStageOne(this, song, card, parent);
		registerListener(listen);
		listen.doTextSearch(song.getArtist(), song.getAlbum(), song.getTitle());
	}

	@Override
	public void gracenoteStageOneComplete(ViewGroup parent, Card card, Song song,
			GNSearchResponse response)
	{
		GracenoteTextResponeListenerStageTwo listen =
				new GracenoteTextResponeListenerStageTwo(this, song, card, parent);
		registerListener(listen);
		if (response != null)
			listen.doFetchByAlbumId(response);

	}

	abstract static class ThumbnailResponseListener
	{
		ResponseReceivedListener updater;
		Song song;
		Card card;
		ViewGroup parent;

		ThumbnailResponseListener(ResponseReceivedListener updater, Song song,
				Card card, ViewGroup parent)
		{
			this.updater = updater;
			this.song = song;
			this.card = card;
			this.parent = parent;
		}

		protected boolean isAlive()
		{
			return updater != null && song != null && card != null && parent != null;
		}

		void releaseReferences()
		{
			updater = null;
			song = null;
			card = null;
			parent = null;
		}
	}

	private static class ThumbnailListener extends ThumbnailResponseListener
			implements Response.Listener<JSONObject>,
			Response.ErrorListener
	{
		ThumbnailListener(ResponseReceivedListener updater, Song song, Card card, ViewGroup parent)
		{
			super(updater, song, card, parent);
		}

		@Override
		public void onResponse(JSONObject obj)
		{
			if (isAlive())
			{
				String url =
						ItunesClient.getImageUrl(obj, song.getArtist());

				song.setAlbumUrl(url);
				if (url != null)
					updater.updateSingleView(parent, card);
				else
					updater.beginGracenote(parent, card, song);
			}
			// releaseReferences();
		}

		@Override
		public void onErrorResponse(VolleyError error)
		{
			if (isAlive())
			{
				updater.beginGracenote(parent, card, song);
			}

			// releaseReferences();
			// Log.d("Lyrics", "Onfailure" + " " + song.getTitle());

		}
	}

	private static class LyricsListener extends ThumbnailResponseListener
			implements Response.Listener<JSONObject>,
			Response.ErrorListener
	{
		LyricsListener(ResponseReceivedListener updater, Song song, Card card, ViewGroup parent)
		{
			super(updater, song, card, parent);
		}

		@Override
		public void onResponse(JSONObject obj)
		{

			// Log.d("Lyrics", "OnSuccess" + " " + song.getTitle());
			if (isAlive())
			{
				Locale locale = Locale.getDefault();
				String shortLyrics = LyricWikiClient.getShortLyric(obj);

				if (shortLyrics.toUpperCase(locale).equals(LYRICS_WIKI_NOT_FOUND.toUpperCase(locale))
						|| shortLyrics.equals(""))
				{
					song.setCanSearchLyrics(true);

					shortLyrics = SEARCH_LYRICS;

					String searchTerms = getSearchTerms(song);

					// We need to allow the user to see the search results if
					// there is no exact match
					// for the lyrics.
					song.setFullLyricsUrl(LYRICS_SEARCH_URL + searchTerms);

				}
				else
				{
					song.setFullLyricsUrl(LyricWikiClient.getFullLyricsUrl(obj));
				}
				song.setShortLyrics(shortLyrics);
				song.setLyricsLoading(false);
				updater.updateSingleView(parent, card);
			}
		}

		@Override
		public void onErrorResponse(VolleyError error)
		{
			if (isAlive())
			{
				//Log.d("Lyrics", "Onfailure" + " " + song.getTitle());
				song.setLyricsLoading(false);
				updater.updateSingleView(parent, card);
			}
			// releaseReferences();
		}

		private String getSearchTerms(Song song)
		{
			Locale locale = Locale.getDefault();
			String searchTerms = URLParamEncoder.encode(song.getArtist().toLowerCase(locale))
					.replace(ESCAPED_SPACE, LYRICS_WIKI_SEARCH_TERMS_CONNECTOR)
					+ LYRICS_WIKI_SEARCH_TERMS_CONNECTOR
					+ URLParamEncoder.encode(normalizeString(song.getTitle().toLowerCase(locale)))
							.replace(ESCAPED_SPACE, LYRICS_WIKI_SEARCH_TERMS_CONNECTOR);

			return searchTerms;
		}

		private String normalizeString(String string)
		{
			return Normalizer
					.normalize(string, Normalizer.Form.NFD)
					.replaceAll("[^\\p{ASCII}]", "");
		}
	}

	private static class GracenoteTextResponeListenerStageTwo
			extends ThumbnailResponseListener
			implements GNSearchResultReady
	{
		GracenoteTextResponeListenerStageTwo(ResponseReceivedListener updater,
				Song song, Card card, ViewGroup parent)
		{
			super(updater, song, card, parent);
		}

		void doFetchByAlbumId(GNSearchResponse response)
		{
			GNOperations.fetchByAlbumId(this, config, response.getAlbumId());
		}

		@Override
		public void GNResultReady(GNSearchResult result)
		{
			GNSearchResponse response = result.getBestResponse();
			if (isAlive())
			{
				if (!result.isTextSearchNoMatchStatus())
				{
					String imageUrl = null;
					GNCoverArt art = response.getCoverArt();
					response.getAlbumId();
					if (art != null)
					{
						imageUrl = art.getUrl();
					}
					song.setAlbumUrl(imageUrl);
					updater.updateSingleView(parent, card);
				}
			}
		}
	}

	private static class GracenoteTextResponeListenerStageOne
			extends ThumbnailResponseListener
			implements GNSearchResultReady
	{

		GracenoteTextResponeListenerStageOne(ResponseReceivedListener updater, Song song,
				Card card, ViewGroup parent)
		{
			super(updater, song, card, parent);
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
				GNSearchResponse response = result.getBestResponse();
				if (isAlive())
				{
					// Log.d("LYRICS", response.getTrackTitle());
					updater.gracenoteStageOneComplete(parent, card, song, response);
				}
			}
		}
	}

	@Override
	public void doLyricsListen(ViewGroup parent, Card card, Song song)
	{
		LyricsListener listen = new LyricsListener(this, song, card,
				parent);
		registerListener(listen);
		LyricWikiClient.get(queue, TAG_VOLLEY, song.getTitle(),
				song.getArtist(), listen, listen);
	}
}