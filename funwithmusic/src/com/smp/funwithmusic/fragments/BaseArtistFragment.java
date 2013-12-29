package com.smp.funwithmusic.fragments;

import static com.smp.funwithmusic.global.Constants.*;

import static com.smp.funwithmusic.global.Constants.TAG_VOLLEY;
import java.util.ArrayList;

import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.activities.ArtistActivity;
import com.smp.funwithmusic.activities.ArtistActivity.DisplayedView;
import com.smp.funwithmusic.fragments.ArtistInfo;
import com.smp.funwithmusic.global.GlobalRequest;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.View;
import android.widget.ViewFlipper;

public abstract class BaseArtistFragment extends Fragment
{
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		flipper = (ViewFlipper) view.findViewById(R.id.flipper);
		listen = getNewListener(getType());
		prepareAdapter();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putSerializable(BUNDLE_FRAGMENT, type);
		outState.putString(BUNDLE_ARTIST_NAME, artist);
	}

	private ArtistInfo type;
	protected BaseArtistListener listen;
	protected String artist;
	protected ArrayList<?> data;
	protected ViewFlipper flipper;

	public void changeFlipperState(int view)
	{
		flipper.setDisplayedChild(view);
	}

	public void setType(ArtistInfo type)
	{
		this.type = type;
	}

	public ArtistInfo getType()
	{
		return type;
	}

	public BaseArtistFragment()
	{
		setRetainInstance(true);
	}

	public static final BaseArtistFragment newInstance(ArtistInfo info)
	{
		BaseArtistFragment frag = null;
		switch (info)
		{
			case IMAGES:
				frag = new ImagesFragment();
				break;
			case BIOGRAPHIES:
				frag = new BiographiesFragment();
				break;
			case EVENTS:
				frag = new EventsFragment();
				break;
			case REVIEWS:
				frag = new NewsReviewsFragment();
				break;
			case NEWS:
				frag = new NewsReviewsFragment();
				break;
			default:
				throw new UnsupportedOperationException("unknown fragment "
						+ info.toString());
		}
		if (info != null)
			frag.setType(info);
		return frag;
	}

	public boolean hasData()
	{
		return (data != null && data.size() > 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (isAdded())
			artist = ((ArtistActivity) getActivity()).getArtist();
		if (savedInstanceState != null)
		{
			type = (ArtistInfo) savedInstanceState.getSerializable(BUNDLE_FRAGMENT);
			artist = savedInstanceState.getString(BUNDLE_ARTIST_NAME);
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		GlobalRequest.getInstance(getActivity())
				.getRequestQueue().cancelAll(TAG_VOLLEY);
		if (listen != null)
		{
			listen.frag = null;
		}
	}

	protected void onDataReceived(ArrayList<?> data)
	{
		this.data = data;
		if (hasData())
		{
			showFrag();
			makeAdapter();
		}
		else
		{
			changeFlipperState((DisplayedView.NOT_FOUND.ordinal()));
		}
	}

	protected void prepareAdapter()
	{
		if (data == null || data.size() == 0)
		{
			changeFlipperState((DisplayedView.LOADING.ordinal()));
			getData();
		}
		else
		{
			showFrag();
			makeAdapter();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	private void showFrag()
	{

		changeFlipperState((DisplayedView.CONTENT.ordinal()));
	}

	protected abstract void makeAdapter();

	protected abstract void getData();

	private BaseArtistListener getNewListener(ArtistInfo type)
	{
		BaseArtistListener listen = null;
		switch (type)
		{
			case IMAGES:
				listen = new ImagesFragment.ImagesListener(this);
				break;
			case BIOGRAPHIES:
				listen = new BiographiesFragment.BiographiesListener(this);
				break;
			case REVIEWS:
				listen = new NewsReviewsFragment.NewsReviewsListener(this);
				break;
			case NEWS:
				listen = new NewsReviewsFragment.NewsReviewsListener(this);
				break;
			case EVENTS:
				listen = new EventsFragment.ArtistIdListener(this);
				break;
			default:
				throw new RuntimeException("unknown fragment");
		}
		return listen;
	}

	protected static class BaseArtistListener implements Response.Listener<JSONObject>,
			Response.ErrorListener
	{
		protected BaseArtistFragment frag;

		protected BaseArtistListener(BaseArtistFragment frag)
		{
			this.frag = frag;
		}

		@Override
		public void onResponse(JSONObject response)
		{

			if (frag != null)
			{
				//Log.d("response", "onResponse called!");
			}
		}

		@Override
		public void onErrorResponse(VolleyError error)
		{

			if (frag != null)
			{
				frag.changeFlipperState((DisplayedView.NOT_FOUND.ordinal()));
				//Log.d("response", "onError called!");
			}
		}
	}
}
