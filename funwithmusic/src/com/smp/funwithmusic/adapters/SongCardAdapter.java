package com.smp.funwithmusic.adapters;

import java.util.List;

import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.apiclient.ItunesClient;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.dataobjects.SongCard;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;

import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.views.image.SilkImageView;

public class SongCardAdapter<SongCard> extends CardAdapter<Card>

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

		final SongCard songCard = (SongCard) card;
		final Song song = ((com.smp.funwithmusic.dataobjects.SongCard) songCard).getSong();

		
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

	@Override
	public View onViewCreated(int index, View recycled, Card item, ViewGroup parent)
	{
		TextView lyrics = (TextView) recycled.findViewById(R.id.lyrics);
		if (lyrics != null)
			lyrics.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla metus libero, iaculis non feugiat nec, tempus vitae mi.");
		return super.onViewCreated(index, recycled, item, parent);
	}
}
