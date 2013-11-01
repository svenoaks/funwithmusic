package com.smp.funwithmusic.fragments;



import com.smp.funwithmusic.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ImagesFragment extends Fragment {
	
	private int mColorRes = -1;
	
	public ImagesFragment() { 
		
		setRetainInstance(true);
	}
	
	public static final ImagesFragment newInstance(int color)
	{
	    ImagesFragment fragment = new ImagesFragment();
	    Bundle bundle = new Bundle();
	    bundle.putInt("testKey", color);
	    fragment.setArguments(bundle);
	    return fragment ;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null)
			mColorRes = savedInstanceState.getInt("mColorRes");
		int color = getResources().getColor(mColorRes);
		// construct the RelativeLayout
		RelativeLayout v = new RelativeLayout(getActivity());
		v.setBackgroundColor(color);	
		
		return v;
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mColorRes = getArguments().getInt("testKey");
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mColorRes", mColorRes);
	}
	
}
