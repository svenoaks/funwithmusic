package com.smp.funwithmusic.adapters;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import static com.smp.funwithmusic.utilities.Constants.*;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.apiclient.ItunesClient;
import com.smp.funwithmusic.apiclient.LyricWikiClient;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.dataobjects.SongCard;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;

public class SongCardAdapter<T extends SongCard> extends CardAdapter<Card>

{
	private Context mContext;

	public SongCardAdapter(Context context)
	{
		super(context, R.layout.card_song); // the custom card layout is passed
											// to the super constructor instead
											// of every individual card
		mContext = context;
	}

	@Override
	protected boolean onProcessThumbnail(final ImageView icon, final Card card, final ViewGroup parent)
	{
		final Song song = ((SongCard) card).getSong();

		Picasso.with(mContext).load(song.getAlbumUrl())
				.placeholder(R.drawable.flow)
				.error(R.drawable.flow)
				.into(icon);

		if (!song.isCantGetAlbumUrl())
		{

			ItunesClient.get(song.getAlbum(), new JsonHttpResponseHandler()
			{
				@Override
				public void onSuccess(JSONObject obj)
				{
					String url =
							ItunesClient.getImageUrl(obj, song.getArtist());

					song.setAlbumUrl(url);
					updateSingleView(parent, card);
				}
			});
			song.setCantGetAlbumUrl(true);
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
		// Optional, you can modify properties of the content textview here.
		return super.onProcessContent(content, card);
	}

	protected boolean onProcessLyrics(TextView lyrics, final Card card, final ViewGroup parent)
	{
		final Song song = ((SongCard) card).getSong();

		if (song.hasLyrics())
		{
			lyrics.setText(song.getShortLyrics());
		}
		else
		{
			lyrics.setText(LYRICS_LOADING);
		}

		if (!song.isCantGetLyrics())
		{
			//Log.d("LYRICS", "in if");
			LyricWikiClient.get(song.getTitle(), song.getArtist(), new AsyncHttpResponseHandler()
			{
				@Override
				public void onSuccess(String text)
				{
					//Log.d("LYRICS", text);
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
						//Log.d("LYRICS", "in obj");
						Locale locale = Locale.getDefault();
						String shortLyrics = LyricWikiClient.getShortLyric(obj);
						//Log.d("LYRICS", shortLyrics.toUpperCase(locale) + " " + NOT_FOUND.toUpperCase(locale) );
						if (shortLyrics.toUpperCase(locale).equals(NOT_FOUND.toUpperCase(locale)))
						{
							shortLyrics += CLICK_TO_ADD;
						}
						song.setShortLyrics(shortLyrics);
						song.setFullLyricsUrl(LyricWikiClient.getFullLyricsUrl(obj));
						updateSingleView(parent, card);
					}

				}
			});
			song.setCantGetLyrics(true);
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
