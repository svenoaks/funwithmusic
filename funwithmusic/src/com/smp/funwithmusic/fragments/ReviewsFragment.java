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
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.dataobjects.Biography;
import com.smp.funwithmusic.dataobjects.Review;
import com.smp.funwithmusic.fragments.BaseArtistFragment.BaseArtistListener;
import com.smp.funwithmusic.global.GlobalRequest;

public class ReviewsFragment extends BaseArtistFragment
{
	public ReviewsFragment()
	{
		super();
	}

	private CardListView listView;
	private ArrayList<Review> reviews;

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
				/*
				 * String bioUrl = reviews.get(index -
				 * CORRECT_FOR_HEADER).getUrl(); Intent intent = new
				 * Intent(getActivity(), WebActivity.class);
				 * intent.putExtra(WEB_URL, bioUrl); startActivity(intent);
				 */
				String bioUrl = reviews.get(index - CORRECT_FOR_HEADER).getUrl();
				Uri uri = Uri.parse(bioUrl);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			}
		});

		if (reviews != null && reviews.size() != 0)
		{
			makeAdapter();
		}
		else
		{
			getreviews();
		}

		return layout;
	}

	private void getreviews()
	{
		EchoNestClient.getArtistInfo(GlobalRequest.getInstance(), TAG_VOLLEY, artist,
				echoNestRequest.REVIEWS, listen, listen);
	}

	private void onreviewsReceived(ArrayList<Review> arrayList)
	{
		this.reviews = arrayList;
		if (reviews == null || reviews.size() == 0)
		{
			viewVisible(((ArtistActivity) getActivity()).getNotFound());
		}
		else
		{
			makeAdapter();
		}
	}

	private void makeAdapter()
	{
		CardAdapter<Card> cardsAdapter = new CardAdapter<Card>(getActivity());
		cardsAdapter.setAccentColorRes(android.R.color.holo_blue_dark);
		cardsAdapter.add(new CardHeader("Reviews"));
		for (Review review : reviews)
		{
			cardsAdapter.add(new Card(review.getName(),
					review.getSummary()));
		}
		listView.setAdapter(cardsAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
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
				((ReviewsFragment) frag).onreviewsReceived((ArrayList<Review>)
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
