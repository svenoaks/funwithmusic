package com.smp.funwithmusic.activities;

import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.R.layout;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventInfo;
import com.smp.funwithmusic.fragments.ColorFragment;
import com.smp.funwithmusic.fragments.ArtistMenuFragment;

import static com.smp.funwithmusic.utilities.Constants.*;
import static com.smp.funwithmusic.utilities.UtilityMethods.*;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;

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

	public ArtistActivity()
	{
		super(R.string.artist);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// set the Above View
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
		{
			Bundle args = new Bundle();
			args.putInt("testKey", R.color.card_gray);

			mContent = new ColorFragment();
			mContent.setArguments(args);

		}

		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, mContent)
				.commit();

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
		
		artist = getIntent().getStringExtra(ARTIST_NAME);
		
		setTitle(artist);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
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
