package com.smp.funwithmusic.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.silk.adapters.SilkAdapter.ViewHolder;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.dataobjects.Performance;

public class EventCardAdapter<T extends EventCard> extends CardAdapter<Card>
{
	
	Context context;

	public EventCardAdapter(Context context)
	{
		super(context, R.layout.list_item_card);
		this.context = context;
	}

	@Override
	public View onViewCreated(int index, View recycled, Card item,
			ViewGroup parent, ViewHolder holder)
	{
		/*
		if (holder.title2 == null)
			holder.title2 = (TextView) recycled.findViewById(R.id.other_artists);
		if (holder.content2 == null)
			holder.content2 = (TextView) recycled.findViewById(R.id.location);
		if (holder.content3 == null)
			holder.content3 = (TextView) recycled.findViewById(R.id.date_time);
		
		if (holder.title2 != null)
			onProcessVenueName(holder.title2, item, parent);
		if (holder.content2 != null)
			onProcessLocation(holder.content2, item, parent);
		if (holder.content3 != null)
			onProcessDateTime(holder.content3, item, parent);
*/
		return super.onViewCreated(index, recycled, item, parent, holder);
	}
	
	private void onProcessDateTime(TextView content3, Card item, ViewGroup parent)
	{
		final Event event = ((EventCard) item).getEvent();
		String date = event.getDateTime();
		if (date == null || date.equals("null"))
			date = event.getDate();
				
		content3.setText(date);
	}

	private void onProcessVenueName(TextView title2, Card item, ViewGroup parent)
	{
		final Event event = ((EventCard) item).getEvent();
		String venueDisplayName = event.getVenueDisplayName();
		title2.setText(venueDisplayName);
	}

	//Artist name
	@Override
	protected boolean onProcessTitle(TextView title, Card card, int accentColor)
	{
		final Event event = ((EventCard) card).getEvent();
		if (title == null)
			return false;
		title.setText(event.getDisplayName());
		title.setTextColor(accentColor);
		return true;
	}
	
	
	@Override
	protected boolean onProcessThumbnail(ImageView icon, Card card, ViewGroup parent)
	{
		final Event event = ((EventCard) card).getEvent();
		return super.onProcessThumbnail(icon, card, parent);
	}
	
	//Other artists at show
	@Override
	protected boolean onProcessContent(TextView content, Card card)
	{
		if (content == null) return false;
		final Event event = ((EventCard) card).getEvent();
		List<Performance> perfs = event.getPerformances();
		StringBuilder str = new StringBuilder();
		for (Performance per : perfs)
		{
			str.append(per.getDisplayName() + "\n");
		}
		content.setText(str.toString());
		return true;
		//return super.onProcessContent(content, card);
	}

	private void onProcessLocation(TextView title2, Card item, ViewGroup parent)
	{
		final Event event = ((EventCard) item).getEvent();
		String location = event.getLocation();
		title2.setText(location);
	}

	
}
