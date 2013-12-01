package com.smp.funwithmusic.fragments;

import java.util.ArrayList;
import static com.smp.funwithmusic.global.Constants.*;
import org.json.JSONObject;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;
import com.android.volley.VolleyError;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.dataobjects.Biography;
import com.smp.funwithmusic.global.GlobalRequest;
import com.smp.funwithmusic.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BiographiesFragment extends BaseArtistFragment
{
	public BiographiesFragment()
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
				 * String bioUrl = bios.get(index -
				 * CORRECT_FOR_HEADER).getUrl(); Intent intent = new
				 * Intent(getActivity(), WebActivity.class);
				 * intent.putExtra(WEB_URL, bioUrl); startActivity(intent);
				 */
				String bioUrl = ((ArrayList<Biography>) data).get(index - CORRECT_FOR_HEADER).getUrl();
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
		EchoNestClient.getArtistInfo(GlobalRequest.getInstance(), TAG_VOLLEY, artist,
				echoNestRequest.BIOGRAPHIES, listen, listen);
	}

	@Override
	protected void makeAdapter()
	{
		CardAdapter<Card> cardsAdapter = new CardAdapter<Card>(getActivity());
		cardsAdapter.setAccentColorRes(android.R.color.holo_blue_dark);
		cardsAdapter.add(new CardHeader("Biographies"));
		
		for (Biography bio : ((ArrayList<Biography>) data))
		{
			cardsAdapter.add(new Card("Biography at " + bio.getSite(),
					bio.getText()));
		}
		listView.setAdapter(cardsAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	static class BiographiesListener extends BaseArtistListener
	{
		BiographiesListener(BaseArtistFragment baseArtistFragment)
		{
			super(baseArtistFragment);
		}

		@Override
		public void onResponse(JSONObject response)
		{
			Log.d("response", "onResponse before frag called!");
			if (frag != null)
			{
				super.onResponse(response);
				((BiographiesFragment) frag).onDataReceived((ArrayList<Biography>)
						EchoNestClient.parseBiographies(response));
			}
		}

		@Override
		public void onErrorResponse(VolleyError error)
		{
			Log.d("response", "onError before frag called!");
			if (frag != null)
			{
				super.onErrorResponse(error);
			}
		}
	}

	
}
