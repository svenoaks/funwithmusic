package com.smp.funwithmusic.fragments;

import java.util.ArrayList;

import static com.smp.funwithmusic.global.Constants.*;

import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.smp.funwithmusic.activities.*;
import com.smp.funwithmusic.adapters.ImagesAdapter;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.fragments.ArtistMenuFragment.ArtistInfo;
import com.smp.funwithmusic.global.GlobalRequest;
import com.smp.funwithmusic.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

public class ImagesFragment extends BaseArtistFragment
{
	public ImagesFragment()
	{
	}

	private UrlListener urlListen;
	private GridView gridView;
	private ArrayList<String> urls;

	@Override
	public void onPause()
	{
		super.onPause();
		if (urlListen != null)
		{
			urlListen.frag = null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			urls = savedInstanceState.getStringArrayList(BUNDLE_IMAGE_URLS);
		}

		LinearLayout layout = (LinearLayout) (inflater.inflate(R.layout.fragment_images, null));
		gridView = (GridView) layout.findViewById(R.id.images_view);
		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id)
			{
				Intent intent = new Intent(getActivity(), WebActivity.class);
				intent.putExtra(WEB_URL, urls.get(position));
				startActivity(intent);
			}
		});

		if (urls == null || urls.size() == 0)
		{
			getUrls();
		}
		else
		{
			makeAdapter();
		}

		return layout;
	}

	private void getUrls()
	{
		urlListen = new UrlListener(this);
		EchoNestClient.getArtistInfo(GlobalRequest.getInstance(), TAG_VOLLEY, artist,
				echoNestRequest.IMAGES, urlListen, urlListen);
	}

	private void onUrlsReceived(ArrayList<String> urls)
	{
		this.urls = urls;
		makeAdapter();
	}

	private static class UrlListener implements Response.Listener<JSONObject>,
			Response.ErrorListener
	{
		ImagesFragment frag;

		UrlListener(ImagesFragment frag)
		{
			this.frag = frag;
		}

		@Override
		public void onResponse(JSONObject response)
		{
			frag.onUrlsReceived((ArrayList<String>)
					EchoNestClient.parseImages(response));
			
			frag = null;
		}

		@Override
		public void onErrorResponse(VolleyError error)
		{
			frag = null;
		}
	}

	private void makeAdapter()
	{
		ImagesAdapter adapter = new ImagesAdapter(getActivity().getApplicationContext(), urls);
		gridView.setAdapter(adapter);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putStringArrayList(BUNDLE_IMAGE_URLS, urls);
	}

}
