package com.smp.funwithmusic.activities;

import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
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
import android.view.Menu;
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

public class ArtistActivity extends SlidingFragmentActivity
{

	private String artist;

	public String getArtist()
	{
		return artist;
	}

	private Fragment mContent;
	private GridView gridView;
	private float dpHeight, dpWidth;
	DisplayMetrics outMetrics;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist);
		artist = getIntent().getStringExtra(ARTIST_NAME);
		setTitle(artist);

		if (savedInstanceState != null)

			mContent =
					getSupportFragmentManager().getFragment(savedInstanceState,
							BUNDLE_FRAGMENT);

		if (mContent == null)
		{
			mContent = ImagesFragment.newInstance();
		}

		configureSlidingMenu();

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent)
				.commit();
		// set the Behind View
		setBehindContentView(R.layout.list_menu_artist);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.menu_frame, new ArtistMenuFragment())
				.commit();

		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	private void configureSlidingMenu()
	{
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.slidingmenu_shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setSlidingActionBarEnabled(false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, BUNDLE_FRAGMENT,
				mContent);
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
			case android.R.id.home:
				toggle();
				break;
			case R.id.clear:

				break;
			case R.id.listen:

				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_artist, menu);
		return true;
	}
}
