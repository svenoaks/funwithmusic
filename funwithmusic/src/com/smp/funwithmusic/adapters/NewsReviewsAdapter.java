package com.smp.funwithmusic.adapters;

import java.util.ArrayList;
import java.util.List;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.android.volley.toolbox.NetworkImageView;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.dataobjects.NewsReview;
import com.smp.funwithmusic.dataobjects.NewsReviewCard;
import com.smp.funwithmusic.global.GlobalRequest;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class NewsReviewsAdapter<T extends NewsReviewCard> extends CardAdapter<Card>
{
	Context context;
	ArrayList<NewsReview> reviews;

	public NewsReviewsAdapter(Context context, ArrayList<NewsReview> data)
	{
		super(context, R.layout.list_item_review);
		this.context = context;
		this.reviews = data;
	}

	@Override
	protected boolean onProcessThumbnail(ImageView icon, Card card, ViewGroup parent)
	{
		final NewsReview event = ((NewsReviewCard) card).getReview();
		if (icon == null)
			return false;

		// if (event.getImage_url() != null
		// && !event.getImage_url().equals(""))
		// {
		Log.d("REVIEW", event.getImage_url());
		if (icon instanceof NetworkImageView)
		{
			NetworkImageView nIcon = (NetworkImageView) icon;
			nIcon.setDefaultImageResId(R.drawable.flow);
			nIcon.setErrorImageResId(R.drawable.flow);
			nIcon.setImageUrl(event.getImage_url(), GlobalRequest
					.getInstance(context).getImageLoader());
		}
		// }
		/*
		 * Picasso.with(context).load(event.getImage_url())
		 * .placeholder(R.drawable.flow) .error(R.drawable.flow) .centerCrop()
		 * .resizeDimen(R.dimen.card_thumbnail_large,
		 * R.dimen.card_thumbnail_large) .into(icon);
		 */
		return true;
	}
}
