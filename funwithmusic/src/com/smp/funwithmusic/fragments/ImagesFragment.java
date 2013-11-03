package com.smp.funwithmusic.fragments;

import java.util.ArrayList;
import java.util.List;
import static com.smp.funwithmusic.utilities.Constants.*;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.activities.*;
import com.smp.funwithmusic.adapters.ImagesAdapter;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
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
import android.widget.RelativeLayout;

public class ImagesFragment extends Fragment
{
	private String artist;
	private GridView gridView;
	private ArrayList<String> urls;
	private int width, height;

	public ImagesFragment()
	{
		setRetainInstance(true);
	}

	public static final ImagesFragment newInstance()
	{
		ImagesFragment fragment = new ImagesFragment();
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
			urls = savedInstanceState.getStringArrayList(BUNDLE_IMAGE_URLS);
		}
		LinearLayout layout = (LinearLayout) (inflater.inflate(R.layout.fragment_images, null));
		gridView = (GridView) layout.findViewById(R.id.images_view);
		if (urls == null)
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
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		final DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		EchoNestClient.getArtistInfo(artist, echoNestRequest.IMAGES, new JsonHttpResponseHandler()
		{

			@Override
			public void onSuccess(JSONObject obj)
			{
				// Log.d("Images", "In Success");
				urls = (ArrayList<String>) EchoNestClient.parseImages(obj);

				width = outMetrics.widthPixels / gridView.getNumColumns();
				height = (int) Math.round((width * 1.5));
				makeAdapter();
			}
		});
	}

	private void makeAdapter()
	{
		ImagesAdapter adapter = new ImagesAdapter(getActivity(), urls, width, height);
		gridView.setAdapter(adapter);
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
		outState.putStringArrayList(BUNDLE_IMAGE_URLS, urls);
	}

}
