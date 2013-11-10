package com.smp.funwithmusic.fragments;

import java.util.ArrayList;
import static com.smp.funwithmusic.utilities.Constants.*;
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
	public ImagesFragment(ArtistInfo type)
	{
		super(ArtistInfo.IMAGES);
	}

	private GridView gridView;
	private ArrayList<String> items;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			items = savedInstanceState.getStringArrayList(BUNDLE_IMAGE_URLS);
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
				intent.putExtra(WEB_URL, items.get(position));
				startActivity(intent);
			}
		});
		final ViewTreeObserver observer = gridView.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout()
			{
				final ViewTreeObserver observer = gridView.getViewTreeObserver();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				{
					observer.removeOnGlobalLayoutListener(this);
				}
				else
				{
					observer.removeGlobalOnLayoutListener(this);
				}
				if (items == null || items.size() == 0)
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
		EchoNestClient.getArtistInfo(queue, TAG_VOLLEY, artist,
				echoNestRequest.IMAGES, new Response.Listener<JSONObject>()
				{
					@Override
					public void onResponse(JSONObject obj)
					{
						// Log.d("Images", "In Success");
						items = (ArrayList<String>) EchoNestClient.parseImages(obj);

						makeAdapter();
					}
				}, new Response.ErrorListener()
				{

					@Override
					public void onErrorResponse(VolleyError error)
					{

					}
				});
	}

	private void makeAdapter()
	{
		ImagesAdapter adapter = new ImagesAdapter(getActivity(), items);
		gridView.setAdapter(adapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putStringArrayList(BUNDLE_IMAGE_URLS, items);
	}

	
}
