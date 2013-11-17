package com.smp.funwithmusic.adapters;

import android.content.Context;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.smp.funwithmusic.dataobjects.EventCard;

public class EventCardAdapter<T extends EventCard> extends CardAdapter<Card>
{
	Context context;

	public EventCardAdapter(Context context)
	{
		super(context);
		this.context = context;
	}

}
