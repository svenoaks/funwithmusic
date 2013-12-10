package com.smp.funwithmusic.adapters;

import java.util.ArrayList;
import java.util.List;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.dataobjects.Review;
import com.smp.funwithmusic.dataobjects.ReviewCard;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ReviewsAdapter<T extends ReviewCard> extends CardAdapter<Card>
{
	Context context;
	ArrayList<Review> reviews;
	public ReviewsAdapter(Context context, ArrayList<Review> data)
	{
		super(context, R.layout.list_item_review);
		this.context = context;
		this.reviews = data;
	}
	
	@Override
	protected boolean onProcessThumbnail(ImageView icon, Card card, ViewGroup parent)
	{
		final Review event = ((ReviewCard) card).getReview();
		if (icon == null)
			return false;
		
		if (event.getImage_url() != null 
				&& !event.getImage_url().equals(""))
		Picasso.with(context).load(event.getImage_url())
				.placeholder(R.drawable.flow)
				.error(R.drawable.flow)
				.centerCrop()
				.resizeDimen(R.dimen.card_thumbnail_large, R.dimen.card_thumbnail_large)
				.into(icon);
		return true;
	}
}
