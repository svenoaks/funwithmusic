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
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class EventsFragment extends BaseArtistFragment
{
	private enum DisplayedView
	{
		LOADING, ARTIST_IMAGE
	};

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
	private Target target;
	private Bitmap artistBitmap;
	private EventCardAdapter<EventCard> cardsAdapter;
	private ViewFlipper flipper;
	private float LAYOUT_HEIGHT_IN_DP;

	@Override
	public void onPause()
	{
		super.onPause();
		if (eventListener != null)
		{
			eventListener.frag = null;
		}
		Picasso.with(getActivity()).cancelRequest(target);
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

		target = new Target()
		{

			@Override
			public void onBitmapFailed(Drawable arg0)
			{

			}

			@Override
			public void onBitmapLoaded(final Bitmap artistBitmap, LoadedFrom arg1)
			{
				ViewTreeObserver vto = flipper.getViewTreeObserver();
				vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
				{
					@Override
					public void onGlobalLayout()
					{
						EventsFragment.this.artistBitmap = artistBitmap;
						FrameLayout frame = (FrameLayout) flipper.findViewById(R.id.image_frame);
						
						scaleFrame(frame, LAYOUT_HEIGHT_IN_DP);
						TextView eventText = (TextView) flipper.findViewById(R.id.concerts_number);
						eventText.setText(String.valueOf(events.size()) + " upcoming concerts");
						if (Build.VERSION.SDK_INT < 16)
						{
							removeLayoutListenerPre16(flipper.getViewTreeObserver(), this);
						}
						else
						{
							removeLayoutListenerPost16(flipper.getViewTreeObserver(), this);
						}
					}
				});

				flipper.setDisplayedChild(DisplayedView.ARTIST_IMAGE.ordinal());
				cardsAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPrepareLoad(Drawable arg0)
			{
				// TODO Auto-generated method stub

			}
		};
		
		LAYOUT_HEIGHT_IN_DP = getActivity()
				.getResources()
				.getDimension(R.dimen.artist_info_pic_height);
		prepareAdapter();

		return layout;
	}

	protected void scaleFrame(FrameLayout frame, float heightOfFrame)
	{
		int width = artistBitmap.getWidth();
		int height = artistBitmap.getHeight();

		final double MAX_PERCENTAGE_OF_SCREEN = 0.75;
		int maxWidth = (int) (frame.getWidth() * MAX_PERCENTAGE_OF_SCREEN);

		float scale = heightOfFrame / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		Bitmap scaledBitmap = Bitmap.createBitmap(artistBitmap, 0, 0, width, height, matrix, true);

		width = scaledBitmap.getWidth();
		height = scaledBitmap.getHeight();

		if (width > maxWidth)
			width = maxWidth;

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
				height);

		frame.setLayoutParams(params);

		ImageView eventImage = (ImageView) flipper.findViewById(R.id.event_image);
		eventImage.setImageBitmap(scaledBitmap);
		frame.setVisibility(View.VISIBLE);
	}

	private int dpToPx(int dp)
	{
		float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

	private void prepareAdapter()
	{
		if (events == null || events.size() == 0)
		{
			getArtistId();
		}
		else
		{
			makeAdapter();
		}
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
		cardsAdapter = new EventCardAdapter<EventCard>(getActivity(), imageUrl);
		cardsAdapter.setAccentColorRes(android.R.color.holo_blue_dark);
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
				flipper = (ViewFlipper) listView.findViewById(R.id.flip_image);
				Picasso.with(getActivity()).load(imageUrl).into(target);
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