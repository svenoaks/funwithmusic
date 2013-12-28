package com.smp.funwithmusic.fragments;

import static com.smp.funwithmusic.global.Constants.TAG_VOLLEY;
import static com.smp.funwithmusic.global.UtilityMethods.viewVisible;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;
import com.android.volley.VolleyError;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.activities.ArtistActivity;
import com.smp.funwithmusic.activities.ArtistActivity.DisplayedView;
import com.smp.funwithmusic.adapters.EventCardAdapter;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.apiclient.SongKickClient;
import com.smp.funwithmusic.dataobjects.Biography;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.fragments.BaseArtistFragment.BaseArtistListener;
import com.smp.funwithmusic.global.ApplicationContextProvider;
import com.smp.funwithmusic.global.GlobalRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

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
		//flipper = (ViewFlipper) layout.findViewById(R.id.flipper);
		//prepareAdapter();

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