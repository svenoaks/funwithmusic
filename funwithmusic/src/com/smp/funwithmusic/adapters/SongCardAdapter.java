package com.smp.funwithmusic.adapters;

import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.apiclient.ItunesClient;
import com.smp.funwithmusic.objects.Song;
import com.smp.funwithmusic.objects.SongCard;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
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
	protected boolean onProcessThumbnail(final ImageView icon, Card card)
	{
		// Optional, you can modify properties of the icon ImageView here.
		// In this case, this view is a SilkImageView in the card_larger.xml
		// layout.
		@SuppressWarnings("unchecked")
		SongCard songCard = (SongCard) card;
		final Song song = ((com.smp.funwithmusic.objects.SongCard) songCard).getSong();
		icon.setImageDrawable(null);
		if (song.hasAlbumUrl())
		{
			Picasso.with(mContext).load(song.getAlbumUrl()).into(icon);
		}
		else
		{																																									
			ItunesClient.get(song.getAlbum(), new JsonHttpResponseHandler()
			{
				public void onSuccess(JSONObject obj)
				{
					String url =
							ItunesClient.getImageUrl(obj, song.getArtist());
					
						song.setAlbumUrl(url);
						Picasso.with(mContext).load(url)
								
								.error(R.drawable.ic_launcher)
								.into(icon);
				
					

				}
			});
		}

		return true;
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
	public View onViewCreated(int index, View recycled, Card item)
	{
		// Optional, you can modify properties of other views that you add to
		// the card layout that aren't the icon, title, content...
		return super.onViewCreated(index, recycled, item);
	}
}
