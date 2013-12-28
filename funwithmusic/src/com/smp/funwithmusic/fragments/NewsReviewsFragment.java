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
import com.smp.funwithmusic.adapters.NewsReviewsAdapter;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.dataobjects.Biography;
import com.smp.funwithmusic.dataobjects.NewsReview;
import com.smp.funwithmusic.dataobjects.NewsReviewCard;
import com.smp.funwithmusic.fragments.BaseArtistFragment.BaseArtistListener;
import com.smp.funwithmusic.global.ApplicationContextProvider;
import com.smp.funwithmusic.global.GlobalRequest;

public class NewsReviewsFragment extends BaseArtistFragment
{
	public NewsReviewsFragment()
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
				String reviewUrl = ((ArrayList<NewsReview>) data).get(index - CORRECT_FOR_HEADER).getUrl();
				Uri uri = Uri.parse(reviewUrl);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			}
		});
		//flipper = (ViewFlipper) layout.findViewById(R.id.flipper);
		//prepareAdapter();

		return layout;
	}

	private echoNestRequest getRequestType()
	{
		echoNestRequest request = null;
		switch (getType())
		{
			case NEWS:
				request = echoNestRequest.NEWS;
				break;
			case REVIEWS:
				request = echoNestRequest.REVIEWS;
				break;
			default:
				throw new IllegalArgumentException("Request not supported");
		}
		return request;
	}

	@Override
	protected void getData()
	{

		EchoNestClient.getArtistInfo(GlobalRequest.getInstance(ApplicationContextProvider.getContext())
				.getRequestQueue(), TAG_VOLLEY, artist,
				getRequestType(), listen, listen);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void makeAdapter()
	{
		if (isAdded())
		{
			String name = getType().toString();
			CardAdapter<Card> cardsAdapter = new NewsReviewsAdapter<NewsReviewCard>
					(getActivity(), (ArrayList<NewsReview>) data, getType());

			cardsAdapter.setAccentColorRes(android.R.color.holo_blue_dark);
			cardsAdapter.add(new CardHeader(name));
			for (NewsReview review : (ArrayList<NewsReview>) data)
			{
				cardsAdapter.add(new NewsReviewCard(review));
			}
			listView.setAdapter(cardsAdapter);
		}
	}

	static class NewsReviewsListener extends BaseArtistListener
	{
		NewsReviewsListener(BaseArtistFragment baseArtistFragment)
		{
			super(baseArtistFragment);
		}

		@Override
		public void onResponse(JSONObject response)
		{
			if (frag != null)
			{
				super.onResponse(response);
				((NewsReviewsFragment) frag).onDataReceived((ArrayList<NewsReview>)
						EchoNestClient.parseNewsReviews(response,
								((NewsReviewsFragment) frag).getRequestType()));
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
