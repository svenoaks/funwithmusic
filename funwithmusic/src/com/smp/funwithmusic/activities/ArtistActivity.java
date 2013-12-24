package com.smp.funwithmusic.activities;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.fragments.ArtistMenuFragment.ArtistInfo;
import com.smp.funwithmusic.fragments.BaseArtistFragment;
import com.smp.funwithmusic.fragments.BiographiesFragment;
import com.smp.funwithmusic.fragments.ImagesFragment;
import com.smp.funwithmusic.fragments.ArtistMenuFragment;
import com.smp.funwithmusic.services.IdentifyMusicService;
import com.smp.funwithmusic.views.ProgressWheel;

import static com.smp.funwithmusic.global.Constants.*;
import static com.smp.funwithmusic.global.UtilityMethods.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

public class ArtistActivity extends SlidingFragmentActivity
{
	public enum DisplayedView
	{
		FRAGMENT, NOT_FOUND, LOADING
	};
	
	private class UpdateActivityReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(ACTION_REMOVE_IDENTIFY))
			{
				progressStopSpin(idDialog);
				viewGone(idDialog);
				boolean successful = intent.getBooleanExtra(EXTRA_LISTEN_SUCCESSFUL, false);
				if (successful)
				{
					boolean fromId = true;
					startFlowActivity(fromId);
				}

			}
		}
	}

	private String artist;
	private IntentFilter filter;
	private UpdateActivityReceiver receiver;
	private BaseArtistFragment mContent;
	private View idDialog;
	
	
	private void startFlowActivity(boolean fromId)
	{
		Intent flowIntent = new Intent(ArtistActivity.this, FlowActivity.class);
		if (fromId) flowIntent.putExtra(EXTRA_SHOULD_SCROLL, true);
		flowIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		ArtistActivity.this.startActivity(flowIntent);
	}
	
	public String getArtist()
	{
		return artist;
	}

	public View getIdDialog()
	{
		return idDialog;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

		if (isMyServiceRunning(this, IdentifyMusicService.class))
		{
			viewVisible(idDialog);
			progressSpin(idDialog);
		}
		else
		{
			viewGone(idDialog);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist);
		artist = getIntent().getStringExtra(ARTIST_NAME);
		idDialog = findViewById(R.id.progress);
		
		if (savedInstanceState != null)
		{
			artist = savedInstanceState.getString(BUNDLE_ARTIST_NAME);
			String type = savedInstanceState.getString(BUNDLE_FRAGMENT);
			mContent = BaseArtistFragment.newInstance
					(ArtistInfo.valueOf(type));
		}
		
		setTitle(artist);
		
		if (mContent == null)
		{
			mContent = BaseArtistFragment.newInstance
					(ArtistInfo.BIOGRAPHIES);
		}

		configureSlidingMenu();

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_frame, mContent)
				.commit();

		// set the Behind View

		setBehindContentView(R.layout.list_menu_artist);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.menu_frame, new ArtistMenuFragment())
				.commit();

		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		filter = new IntentFilter();
		filter.addAction(ACTION_ADD_SONG);
		filter.addAction(ACTION_REMOVE_IDENTIFY);
		filter.addCategory(Intent.CATEGORY_DEFAULT);

		receiver = new UpdateActivityReceiver();
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
		outState.putString(BUNDLE_FRAGMENT, mContent.getType().name());
		outState.putString(BUNDLE_ARTIST_NAME, getArtist());
	}

	@Override
	public void onBackPressed()
	{
		
		FragmentManager mgr = getSupportFragmentManager();
		int c = mgr.getBackStackEntryCount();
		if (c == 0)
		{
			finish();
			return;
		}
		if (c > 0)
		{
			BackStackEntry lastFrag =
					mgr.getBackStackEntryAt(c - 1);
			mgr.popBackStackImmediate();
			c = mgr.getBackStackEntryCount();
			if (lastFrag
					.getName()
					.equals(mContent.getType()
							.toString()))
			{
				if (c == 0)
				{
					finish();
				}
				else
				{
					mgr.popBackStackImmediate();
				}
			}
			Log.d("STACK", String.valueOf(c));
		}
		mContent = (BaseArtistFragment) mgr.findFragmentById(R.id.fragment_frame);
		
	}

	public void switchContent(ArtistInfo info)
	{
		FragmentManager mgr = getSupportFragmentManager();
		
		BaseArtistFragment newContent = null;
		try
		{
			newContent = BaseArtistFragment.newInstance(info);
		}
		catch (UnsupportedOperationException e)
		{
			Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show();
			return;
		}
		FragmentTransaction trans = mgr.beginTransaction();
		if (mContent.hasData())
		{
			boolean shouldAdd = true;
			for (int i = 0; i < mgr.getBackStackEntryCount(); ++i)
			{
				if (mContent.getType().toString()
						.equals(mgr.getBackStackEntryAt(i).getName()))
				{
					shouldAdd = false;
					break;
				}

			}
			if (shouldAdd)
				trans.addToBackStack(mContent.getType().toString());
		}
		trans.replace(R.id.fragment_frame, newContent)
				.commit();
		getSlidingMenu().showContent();
		mContent = newContent;
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
				doDeleteFlow(this);
				break;
			case R.id.listen:
				if (!isMyServiceRunning(this, IdentifyMusicService.class))
				{
					doListen(getApplicationContext(), idDialog);
				}
				break;
			case R.id.flow:
				boolean fromId = false;
				startFlowActivity(fromId);
				finish();
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

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
