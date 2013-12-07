package com.smp.funwithmusic.fragments;

import java.util.ArrayList;

import static com.smp.funwithmusic.global.Constants.*;
import static com.smp.funwithmusic.global.UtilityMethods.viewVisible;

import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.smp.funwithmusic.activities.*;
import com.smp.funwithmusic.activities.ArtistActivity.DisplayedView;
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

	private GridView gridView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		
		LinearLayout layout = (LinearLayout) (inflater.inflate(R.layout.fragment_images, null));
		gridView = (GridView) layout.findViewById(R.id.images_view);
		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id)
			{
				Intent intent = new Intent(getActivity(), ImageActivity.class);
				intent.putExtra(WEB_URL, ((ArrayList<String>) data).get(position));
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
				echoNestRequest.IMAGES, listen, listen);
	}

	
	@Override
	protected void makeAdapter()
	{
		ImagesAdapter adapter = new ImagesAdapter(getActivity().getApplicationContext(),
				(ArrayList<String>) data);
		gridView.setAdapter(adapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		// outState.putStringArrayList(BUNDLE_IMAGE_URLS, urls);
	}

	static class ImagesListener extends BaseArtistListener
	{
		ImagesListener(BaseArtistFragment baseArtistFragment)
		{
			super(baseArtistFragment);
		}

		@Override
		public void onResponse(JSONObject response)
		{
			if (frag != null)
			{
				super.onResponse(response);
				((ImagesFragment) frag).onDataReceived((ArrayList<String>)
						EchoNestClient.parseImages(response));
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
