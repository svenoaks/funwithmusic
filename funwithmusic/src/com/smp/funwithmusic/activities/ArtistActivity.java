package com.smp.funwithmusic.activities;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.fragments.BiographiesFragment;
import com.smp.funwithmusic.fragments.ImagesFragment;
import com.smp.funwithmusic.fragments.ArtistMenuFragment;
import com.smp.funwithmusic.services.IdentifyMusicService;

import static com.smp.funwithmusic.utilities.Constants.*;
import static com.smp.funwithmusic.utilities.UtilityMethods.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;

public class ArtistActivity extends SlidingFragmentActivity
{
	private class UpdateActivityReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(ACTION_REMOVE_IDENTIFY))
			{
				FlowActivity.viewGone(loadingDialog);
				boolean successful = intent.getBooleanExtra(EXTRA_LISTEN_SUCCESSFUL, false);
				if (successful)
					ArtistActivity.this.startActivity(new Intent
							(ArtistActivity.this, FlowActivity.class));
			}
		}

	}

	private String artist;

	public String getArtist()
	{
		return artist;
	}

	private IntentFilter filter;
	private UpdateActivityReceiver receiver;
	private Fragment mContent;
	DisplayMetrics outMetrics;
	private View loadingDialog;
	TextView progressText;
	Bundle savedFrags;

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
			FlowActivity.viewVisible(loadingDialog);
		}
		else
		{
			FlowActivity.viewGone(loadingDialog);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist);
		artist = getIntent().getStringExtra(ARTIST_NAME);
		loadingDialog = findViewById(R.id.progress);
		progressText = (TextView) findViewById(R.id.progress_text);
		progressText.setText(getResources().getText(R.string.identify));
		// FlowActivity.viewVisible(loadingDialog);
		setTitle(artist);

		if (savedInstanceState != null)

			mContent =
					getSupportFragmentManager().getFragment(savedInstanceState,
							BUNDLE_FRAGMENT);

		if (mContent == null)
		{
			mContent = ImagesFragment.newInstance(null);
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

		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		filter = new IntentFilter();
		filter.addAction(ACTION_ADD_SONG);
		filter.addAction(ACTION_REMOVE_IDENTIFY);
		filter.addCategory(Intent.CATEGORY_DEFAULT);

		receiver = new UpdateActivityReceiver();
		savedFrags = new Bundle();
		if (savedInstanceState != null)
		{

		}
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

	private void saveCurrentFrag()
	{
		FragmentManager mgr = getSupportFragmentManager();
		String key = null;

		if (mContent instanceof ImagesFragment)
			key = BUNDLE_IMAGESFRAGMENT;
		else if (mContent instanceof BiographiesFragment)
			key = BUNDLE_BIOSFRAGMENT;

		savedFrags.putParcelable(key, mgr.saveFragmentInstanceState(mContent));
	}

	public void switchContent(int position)
	{
		saveCurrentFrag();
		FragmentManager mgr = getSupportFragmentManager();
		SavedState state = null;

		Fragment newContent = null;
		switch (position)
		{
			case 0:
				break;
			case 1:
				break;
			case 2:
				state = savedFrags.getParcelable(BUNDLE_BIOSFRAGMENT);
				newContent = BiographiesFragment.newInstance(state);
				break;
			case 3:
				break;
			case 4:
				state = savedFrags.getParcelable(BUNDLE_IMAGESFRAGMENT);
				newContent = ImagesFragment.newInstance(state);
				break;
			default:

		}

		mContent = newContent;
		mgr
				.beginTransaction()
				.replace(R.id.fragment_frame, mContent)
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
				FlowActivity.doDeleteFlow(this);
				break;
			case R.id.listen:
				if (!isMyServiceRunning(this, IdentifyMusicService.class))
				{
					progressText.setText(getResources().getText(R.string.identify));
					FlowActivity.doListen(this, loadingDialog);
				}
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
