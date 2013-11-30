package com.smp.funwithmusic.adapters;

import java.util.List;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.dataobjects.Review;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ReviewsAdapter<T extends EventCard> extends CardAdapter<Card>
{

	public ReviewsAdapter(Context context)
	{
		super(context);
	}
	

}
