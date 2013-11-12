package com.smp.funwithmusic.fragments;

import static com.smp.funwithmusic.global.Constants.TAG_VOLLEY;

import com.smp.funwithmusic.activities.ArtistActivity;
import com.smp.funwithmusic.fragments.ArtistMenuFragment.ArtistInfo;
import com.smp.funwithmusic.global.GlobalRequest;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseArtistFragment extends Fragment
{
	private ArtistInfo type;
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
				frag.setType(info);
				break;
			case BIOGRAPHIES:
				frag = new BiographiesFragment();
				frag.setType(info);
				break;
			default:
				throw new RuntimeException("unknown fragment " + info.toString());
		}
		//frag.setInitialSavedState(state);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		artist = ((ArtistActivity) getActivity()).getArtist();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		GlobalRequest.getInstance().cancelAll(TAG_VOLLEY);
		//GlobalRequest.getInstance().stop();
		
		//queue = null;
	}
	@Override
	public void onResume()
	{
		super.onResume();
		
		//queue = null;
	}

	
}
