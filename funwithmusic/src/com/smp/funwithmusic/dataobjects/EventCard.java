package com.smp.funwithmusic.dataobjects;

import com.afollestad.cardsui.Card;

import android.content.Context;

public class EventCard extends Card
{
	private static final long serialVersionUID = -5823986754805319080L;
	private Event event;

	public Event getEvent()
	{
		return event;
	}

	public EventCard(final Event event)
	{
		super();
		this.event = event;
	}
}
