package com.smp.funwithmusic.fragments;

import static com.smp.funwithmusic.global.Constants.TAG_VOLLEY;
import static com.smp.funwithmusic.global.UtilityMethods.viewVisible;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;
import com.android.volley.VolleyError;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.activities.ArtistActivity;
import com.smp.funwithmusic.activities.ArtistActivity.DisplayedView;
import com.smp.funwithmusic.adapters.ReviewsAdapter;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.dataobjects.Biography;
import com.smp.funwithmusic.dataobjects.Review;
import com.smp.funwithmusic.dataobjects.ReviewCard;
import com.smp.funwithmusic.fragments.BaseArtistFragment.BaseArtistListener;
import com.smp.funwithmusic.global.GlobalRequest;

public class ReviewsFragment extends BaseArtistFragment
{
	public ReviewsFragment()
	{
		super();
	}

	private CardListView listView;

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
				/*
				 * String bioUrl = reviews.get(index -
				 * CORRECT_FOR_HEADER).getUrl(); Intent intent = new
				 * Intent(getActivity(), WebActivity.class);
				 * intent.putExtra(WEB_URL, bioUrl); startActivity(intent);
				 */
				String bioUrl = ((ArrayList<Review>) data).get(index - CORRECT_FOR_HEADER).getUrl();
				Uri uri = Uri.parse(bioUrl);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			}
		});

		prepareAdapter();

		return layout;
	}

	@Override
	protected void getData()
	{
		EchoNestClient.getArtistInfo(GlobalRequest.getInstance(getActivity())
				.getRequestQueue(), TAG_VOLLEY, artist,
				echoNestRequest.REVIEWS, listen, listen);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void makeAdapter()
	{
		CardAdapter<Card> cardsAdapter = new ReviewsAdapter<ReviewCard>
				(getActivity(), (ArrayList<Review>) data);

		cardsAdapter.setAccentColorRes(android.R.color.holo_blue_dark);
		cardsAdapter.add(new CardHeader("Reviews"));
		for (Review review : (ArrayList<Review>) data)
		{
			cardsAdapter.add(new ReviewCard(review));
		}
		listView.setAdapter(cardsAdapter);
	}

	static class ReviewsListener extends BaseArtistListener
	{
		ReviewsListener(BaseArtistFragment baseArtistFragment)
		{
			super(baseArtistFragment);
		}

		@Override
		public void onResponse(JSONObject response)
		{
			if (frag != null)
			{
				super.onResponse(response);
				((ReviewsFragment) frag).onDataReceived((ArrayList<Review>)
						EchoNestClient.parseReviews(response));
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
