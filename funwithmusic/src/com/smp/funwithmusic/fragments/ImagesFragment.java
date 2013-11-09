package com.smp.funwithmusic.fragments;

import java.util.ArrayList;
import java.util.List;
import static com.smp.funwithmusic.utilities.Constants.*;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.activities.*;
import com.smp.funwithmusic.adapters.ImagesAdapter;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ImagesFragment extends Fragment
{
	
	private final double HEIGHT_VS_WIDTH = 1.5;

	private String artist;
	private GridView gridView;
	private ArrayList<String> urls;
	private int width, height;

	public ImagesFragment()
	{
		setRetainInstance(true);
	}

	public static final ImagesFragment newInstance(SavedState state)
	{
		ImagesFragment fragment = new ImagesFragment();
		fragment.setInitialSavedState(state);
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
		final ViewTreeObserver observer = gridView.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout()
			{
				final ViewTreeObserver observer = gridView.getViewTreeObserver();
		        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		            observer.removeOnGlobalLayoutListener(this);
		        } else {
		            observer.removeGlobalOnLayoutListener(this);
		        }
		        if (urls == null)
				{
					getUrls();
				}
				else
				{
					makeAdapter();
				}
			}
		});
		return layout;
	}

	private void getUrls()
	{
		EchoNestClient.getArtistInfo(Volley.newRequestQueue(getActivity()), artist,
				echoNestRequest.IMAGES, new Response.Listener<JSONObject>()
				{
					@Override
					public void onResponse(JSONObject obj)
					{
						// Log.d("Images", "In Success");
						urls = (ArrayList<String>) EchoNestClient.parseImages(obj);

						makeAdapter();
					}
				}, new Response.ErrorListener()
				{

					@Override
					public void onErrorResponse(VolleyError error)
					{
						// TODO Auto-generated method stub

					}
				});
	}

	private void makeAdapter()
	{
		ImagesAdapter adapter = new ImagesAdapter(getActivity(), urls);
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

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

}
