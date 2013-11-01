package com.smp.funwithmusic.activities;

import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.R.layout;
import com.smp.funwithmusic.adapters.ImagesAdapter;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.apiclient.EchoNestClient.echoNestRequest;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventInfo;
import com.smp.funwithmusic.fragments.ImagesFragment;
import com.smp.funwithmusic.fragments.ArtistMenuFragment;

import static com.smp.funwithmusic.utilities.Constants.*;
import static com.smp.funwithmusic.utilities.UtilityMethods.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.Window;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class ArtistActivity extends BaseActivity
{
	private String artist;
	private Fragment mContent;
	private GridView gridView;
	private float dpHeight, dpWidth;
	DisplayMetrics outMetrics;
	public ArtistActivity()
	{
		super(R.string.artist);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		artist = getIntent().getStringExtra(ARTIST_NAME);
		// set the Above View
		if (savedInstanceState != null)
			//mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
		{
			Bundle args = new Bundle();
			args.putInt("testKey", R.color.card_gray);

			mContent = new ImagesFragment();
			mContent.setArguments(args);

		}
		
		Display display = getWindowManager().getDefaultDisplay();
	    outMetrics = new DisplayMetrics ();
	    display.getMetrics(outMetrics);

	    float density  = getResources().getDisplayMetrics().density;
	    dpHeight = outMetrics.heightPixels / density;
	    dpWidth  = outMetrics.widthPixels / density;
	   
		// set the Above View
		setContentView(R.layout.content_frame);
		gridView = (GridView) findViewById(R.id.images_view);
		EchoNestClient.getArtistInfo(artist, echoNestRequest.IMAGES, new JsonHttpResponseHandler()
		{
			
			@Override
			public void onSuccess(JSONObject obj)
			{
				//Log.d("Images", "In Success");
				List<String> urls = EchoNestClient.parseImages(obj);
				for (String url : urls)
				{
					Log.d("Images", url + "\n");
				}
				int width = outMetrics.widthPixels / gridView.getNumColumns();
				int height = (int)Math.round((width * 1.5));
				
				ImagesAdapter adapter = new ImagesAdapter(ArtistActivity.this, urls, width, height);
				gridView.setAdapter(adapter);
				//adapter.notifyDataSetChanged();
				
			}
		});
		/*
		 * getSupportFragmentManager() .beginTransaction()
		 * .replace(R.id.content_frame, mContent) .commit();
		 */
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.menu_frame, new ArtistMenuFragment())
				.commit();

		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		

		setTitle(artist);
		
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		//getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	public void switchContent(Fragment fragment)
	{
		mContent = fragment;
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, fragment)
				.commit();
		getSlidingMenu().showContent();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.clear:

				break;
			case R.id.listen:

				break;
			default:
				return false;
		}
		return true;
	}

}
