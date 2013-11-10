package com.smp.funwithmusic.fragments;

import static com.smp.funwithmusic.utilities.Constants.TAG_VOLLEY;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.smp.funwithmusic.activities.ArtistActivity;
import com.smp.funwithmusic.fragments.ArtistMenuFragment.ArtistInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseArtistFragment extends Fragment
{
	private ArtistInfo type;
	protected String artist;
	protected RequestQueue queue;

	public ArtistInfo getType()
	{
		return type;
	}
	
	public BaseArtistFragment(ArtistInfo type)
	{
		this.type = type;
		setRetainInstance(true);
	}

	public static final BaseArtistFragment newInstance(ArtistInfo info, SavedState state)
	{
		BaseArtistFragment frag = null;
		switch (info)
		{
			case IMAGES:
				frag = new ImagesFragment(info);
				break;
			case BIOGRAPHIES:
				frag = new BiographiesFragment(info);
				break;
			default:
				throw new RuntimeException("unknown fragment " + info.toString());
		}
		frag.setInitialSavedState(state);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		queue = Volley.newRequestQueue(getActivity().getApplicationContext());
		artist = ((ArtistActivity) getActivity()).getArtist();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		queue.cancelAll(TAG_VOLLEY);
	}
}
