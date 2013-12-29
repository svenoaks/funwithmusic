package com.smp.funwithmusic.fragments;

import static com.smp.funwithmusic.global.Constants.TAG_VOLLEY;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;
import com.android.volley.VolleyError;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.adapters.EventCardAdapter;
import com.smp.funwithmusic.apiclient.SongKickClient;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.global.ApplicationContextProvider;
import com.smp.funwithmusic.global.GlobalRequest;

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
	private EventsListener eventListener;
	private String imageUrl;
	private EventCardAdapter<EventCard> cardsAdapter;

	@Override
	public void onPause()
	{
		super.onPause();

	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (eventListener != null)
		{
			eventListener.frag = null;
		}
		if (cardsAdapter != null)
		{
			cardsAdapter.cancelPicasso();
		}

	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View layout = inflater.inflate(R.layout.fragment_cards_list, null);
		listView = (CardListView) layout.findViewById(R.id.cardsList);
		listView.setOnCardClickListener(new CardListView.CardClickListener()
		{
			@SuppressWarnings("rawtypes")
			@Override
			public void onCardClick(int index, CardBase card, View view)
			{
				final int CORRECT_FOR_HEADER = 1;

				String eventUrl = ((ArrayList<Event>) data)
						.get(index - CORRECT_FOR_HEADER).getMainUri();
				Uri uri = Uri.parse(eventUrl);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		// flipper = (ViewFlipper) layout.findViewById(R.id.flipper);
		// prepareAdapter();

		return layout;
	}

	@Override
	protected void getData()
	{
		SongKickClient.getId(GlobalRequest.getInstance(ApplicationContextProvider.getContext())
				.getRequestQueue(), TAG_VOLLEY, artist,
				listen, listen);
	}

	private void onIdReceived(String artistId)
	{
		eventListener = new EventsListener(this);
		imageUrl = SongKickClient.getImageUrl(artistId);
		SongKickClient.getEvents(GlobalRequest.getInstance(ApplicationContextProvider.getContext())
				.getRequestQueue(), TAG_VOLLEY, artistId,
				eventListener, eventListener);
	}

	@Override
	protected void makeAdapter()
	{
		if (isAdded())
		{
			cardsAdapter = new EventCardAdapter<EventCard>(getActivity(), imageUrl, data.size());
			cardsAdapter.setAccentColorRes(android.R.color.holo_blue_dark);
			CardHeader header = new CardHeader("Upcoming Events");

			cardsAdapter.add(header);

			for (int i = 0; i < data.size(); ++i)
			{
				Event event = ((ArrayList<Event>) data).get(i);
				cardsAdapter.add(new EventCard(event));
			}
			listView.setAdapter(cardsAdapter);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
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
				frag.onDataReceived(SongKickClient
						.parseEvents(response));
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
}