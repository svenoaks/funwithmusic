package com.smp.funwithmusic.fragments;

import static com.smp.funwithmusic.global.Constants.TAG_VOLLEY;
import static com.smp.funwithmusic.global.UtilityMethods.viewVisible;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;
import com.android.volley.VolleyError;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.activities.ArtistActivity;
import com.smp.funwithmusic.adapters.EventCardAdapter;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.apiclient.SongKickClient;
import com.smp.funwithmusic.dataobjects.Biography;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.fragments.BaseArtistFragment.BaseArtistListener;
import com.smp.funwithmusic.global.GlobalRequest;
import com.squareup.picasso.Picasso;

public class EventsFragment extends BaseArtistFragment
{
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

	}

	public EventsFragment()
	{
		super();
	}

	private CardListView listView;
	private ArrayList<Event> events;
	private EventsListener eventListener;
	private String artistId;
	private String imageUrl;

	@Override
	public void onPause()
	{
		super.onPause();
		if (eventListener != null)
		{
			eventListener.frag = null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View layout = inflater.inflate(R.layout.fragment_cards_list, null);
		listView = (CardListView) layout.findViewById(R.id.cardsList);
		listView.setOnCardClickListener(new CardListView.CardClickListener()

		{
			@SuppressWarnings("rawtypes")
			@Override
			public void onCardClick(int index, CardBase card, View view)
			{
				final int CORRECT_FOR_HEADER = 1;

				String eventUrl = events.get(index - CORRECT_FOR_HEADER).getMainUri();
				Uri uri = Uri.parse(eventUrl);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		if (events != null && events.size() != 0)
		{
			makeAdapter();
		}
		else
		{
			getArtistId();
		}

		return layout;
	}

	private void getArtistId()
	{
		SongKickClient.getId(GlobalRequest.getInstance(), TAG_VOLLEY, artist,
				listen, listen);
	}

	private void onEventsReceived(ArrayList<Event> events)
	{
		this.events = events;
		if (events == null || events.size() == 0)
		{
			viewVisible(((ArtistActivity) getActivity()).getNotFound());
		}
		else
		{
			makeAdapter();
		}
	}

	private void onIdReceived(String artistId)
	{
		eventListener = new EventsListener(this);
		this.artistId = artistId;
		imageUrl = SongKickClient.getImageUrl(artistId);
		SongKickClient.getEvents(GlobalRequest.getInstance(), TAG_VOLLEY, artistId,
				eventListener, eventListener);
	}

	private void makeAdapter()
	{
		EventCardAdapter<EventCard> cardsAdapter = new EventCardAdapter<EventCard>(getActivity(), imageUrl);
		// cardsAdapter.setAccentColorRes(android.R.color.holo_blue_dark);
		CardHeader header = new CardHeader("Upcoming Events");
		cardsAdapter.add(header);

		for (int i = 0; i < events.size(); ++i)
		{
			Event event = events.get(i);
			cardsAdapter.add(new EventCard(event));
		}
		listView.setAdapter(cardsAdapter);
		ViewTreeObserver vto = listView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				ImageView eventImage = (ImageView) getActivity().findViewById(R.id.event_image);
				Picasso.with(getActivity()).load(imageUrl).into(eventImage);
				if (Build.VERSION.SDK_INT < 16)
				{
					removeLayoutListenerPre16(listView.getViewTreeObserver(), this);
				}
				else
				{
					removeLayoutListenerPost16(listView.getViewTreeObserver(), this);
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void removeLayoutListenerPre16(ViewTreeObserver observer, OnGlobalLayoutListener listener)
	{
		observer.removeGlobalOnLayoutListener(listener);
	}

	@SuppressLint("NewApi")
	private void removeLayoutListenerPost16(ViewTreeObserver observer, OnGlobalLayoutListener listener)
	{
		observer.removeOnGlobalLayoutListener(listener);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		// outState.putParcelableArrayList(BUNDLE_BIOGRAPHIES, bios);
	}

	static class ArtistIdListener extends BaseArtistListener
	{
		ArtistIdListener(BaseArtistFragment baseArtistFragment)
		{
			super(baseArtistFragment);
		}

		@Override
		public void onResponse(JSONObject response)
		{
			if (frag != null)
			{
				super.onResponse(response);
				((EventsFragment) frag).onIdReceived(
						SongKickClient.parseId(response));
			}
		}

		@Override
		public void onErrorResponse(VolleyError error)
		{
			if (frag != null)
			{
				super.onErrorResponse(error);
			}
		}
	}

	static class EventsListener extends BaseArtistListener
	{
		EventsListener(BaseArtistFragment baseArtistFragment)
		{
			super(baseArtistFragment);
		}

		@Override
		public void onResponse(JSONObject response)
		{
			if (frag != null)
			{
				super.onResponse(response);
				((EventsFragment) frag).onEventsReceived(
						SongKickClient.parseEvents(response));
			}
		}

		@Override
		public void onErrorResponse(VolleyError error)
		{
			if (frag != null)
			{
				super.onErrorResponse(error);
			}
		}
	}

	@Override
	public boolean hasData()
	{
		return events != null && events.size() > 0;
	}
}
