package com.smp.funwithmusic.fragments;

import static com.smp.funwithmusic.global.Constants.BUNDLE_FRAGMENT;
import static com.smp.funwithmusic.global.Constants.TAG_VOLLEY;
import static com.smp.funwithmusic.global.UtilityMethods.*;

import java.util.ArrayList;

import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smp.funwithmusic.activities.ArtistActivity;
import com.smp.funwithmusic.fragments.ArtistMenuFragment.ArtistInfo;
import com.smp.funwithmusic.global.GlobalRequest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class BaseArtistFragment extends Fragment
{
	private ArtistInfo type;
	protected BaseArtistListener listen;
	protected String artist;

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
				frag = new ReviewsFragment();
				break;
			default:
				throw new RuntimeException("unknown fragment " + info.toString());
		}
		frag.setType(info);
		return frag;
	}

	public abstract boolean hasData();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		artist = ((ArtistActivity) getActivity()).getArtist();
		listen = getNewListener(type);
		viewVisible(((ArtistActivity) getActivity()).getLoadingDialog());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		viewGone(((ArtistActivity) getActivity()).getNotFound());
		return null;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		GlobalRequest.getInstance().cancelAll(TAG_VOLLEY);
		if (listen != null)
		{
			listen.frag = null;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

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
				listen = new ReviewsFragment.ReviewsListener(this);
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
			viewGone(((ArtistActivity) frag.getActivity()).getLoadingDialog());
		}

		@Override
		public void onErrorResponse(VolleyError error)
		{
			viewGone(((ArtistActivity) frag.getActivity()).getLoadingDialog());
			viewVisible(((ArtistActivity) frag.getActivity()).getNotFound());
		}

	}
}
