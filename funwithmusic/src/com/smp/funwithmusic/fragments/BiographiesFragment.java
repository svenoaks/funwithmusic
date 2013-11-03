package com.smp.funwithmusic.fragments;

import java.util.ArrayList;
import java.util.List;
import static com.smp.funwithmusic.utilities.Constants.*;
import org.json.JSONObject;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.activities.*;
import com.smp.funwithmusic.adapters.BiographiesAdapter;
import com.smp.funwithmusic.adapters.ImagesAdapter;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.dataobjects.Biography;
import com.smp.funwithmusic.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class BiographiesFragment extends Fragment
{
	private String artist;
	private CardListView listView;
	private ArrayList<Biography> bios;

	public BiographiesFragment()
	{
		setRetainInstance(true);
	}

	public static final BiographiesFragment newInstance()
	{
		BiographiesFragment fragment = new BiographiesFragment();
		Bundle bundle = new Bundle();
		// bundle.putInt("testKey", color);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			bios = savedInstanceState.getParcelableArrayList(BUNDLE_BIOGRAPHIES);
		}
		View layout = inflater.inflate(R.layout.fragment_biographies, null);
		listView = (CardListView) layout.findViewById(R.id.cardsList);
		
		if (bios == null)
		{
			getBios();
		}
		else
		{
			makeAdapter();
		}
		return layout;
	}

	private void getBios()
	{
		
		EchoNestClient.getArtistInfo(artist, echoNestRequest.BIOGRAPHIES, new JsonHttpResponseHandler()
		{

			@Override
			public void onSuccess(JSONObject obj)
			{
				// Log.d("Images", "In Success");
				bios = (ArrayList<Biography>) EchoNestClient.parseBiographies(obj);

				makeAdapter();
			}
		});
	}

	private void makeAdapter()
	{
		CardAdapter<Card> cardsAdapter = new CardAdapter<Card>(getActivity());
		cardsAdapter.setAccentColorRes(android.R.color.holo_blue_dark);
		cardsAdapter.add(new CardHeader("Biographies"));
		for (Biography bio : bios)
		{
			cardsAdapter.add(new Card("From: " + bio.getSite(), 
					bio.getText()));
		}
		listView.setAdapter(cardsAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		artist = ((ArtistActivity) getActivity()).getArtist();

	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(BUNDLE_BIOGRAPHIES, bios);
	}

}
