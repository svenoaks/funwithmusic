package com.smp.funwithmusic.adapters;

import java.util.ArrayList;
import java.util.List;
import static com.smp.funwithmusic.global.UtilityMethods.*;
import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.silk.adapters.SilkAdapter.ViewHolder;
import com.android.volley.toolbox.NetworkImageView;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.dataobjects.NewsReview;
import com.smp.funwithmusic.dataobjects.NewsReviewCard;
import com.smp.funwithmusic.fragments.ArtistMenuFragment.ArtistInfo;
import com.smp.funwithmusic.global.GlobalRequest;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsReviewsAdapter<T extends NewsReviewCard> extends CardAdapter<Card>
{
	Context context;
	ArrayList<NewsReview> reviews;
	ArtistInfo type;

	public NewsReviewsAdapter(Context context, ArrayList<NewsReview> data, ArtistInfo type)
	{
		super(context, R.layout.list_item_review);
		this.context = context;
		this.reviews = data;
		this.type = type;
	}

	@Override
	public View onViewCreated(int index, View recycled, Card item,
			ViewGroup parent, ViewHolder holder)
	{
		if (holder.content2 == null)
			holder.content2 = (TextView) recycled.findViewById(R.id.date);

		if (holder.content2 != null)
			onProcessDate(holder.content2, item, parent);

		return super.onViewCreated(index, recycled, item, parent, holder);
	}

	private void onProcessDate(TextView text, Card item, ViewGroup parent)
	{
		final NewsReview event = ((NewsReviewCard) item).getReview();
		text.setText(processDateTime(event.getDate(), false));
	}

	@Override
	protected boolean onProcessThumbnail(ImageView icon, Card card, ViewGroup parent)
	{
		final NewsReview event = ((NewsReviewCard) card).getReview();
		if (icon == null)
			return false;

		if (type == ArtistInfo.NEWS)
			GlobalRequest.getInstance(context).getPicasso().load(R.drawable.news).fit().into(icon);
		else if (type == ArtistInfo.REVIEWS)
			GlobalRequest.getInstance(context).getPicasso().load(R.drawable.review).fit().into(icon);

		return true;
	}
}
