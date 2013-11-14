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
				progressStopSpin(idDialog);
				viewGone(idDialog);
				boolean successful = intent.getBooleanExtra(EXTRA_LISTEN_SUCCESSFUL, false);
				if (successful)
				{
					Intent flowIntent = new Intent(ArtistActivity.this, FlowActivity.class);
					flowIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					ArtistActivity.this.startActivity(flowIntent);
				}

			}
		}
	}

	private String artist;
	private IntentFilter filter;
	private UpdateActivityReceiver receiver;
	private BaseArtistFragment mContent;
	private View idDialog;
	private View loadingDialog;
	private View notFound;

	public String getArtist()
	{
		return artist;
	}

	public View getLoadingDialog()
	{
		return loadingDialog;
	}

	public View getIdDialog()
	{
		return idDialog;
	}

	public View getNotFound()
	{
		return notFound;
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
		loadingDialog = findViewById(R.id.loading);
		notFound = findViewById(R.id.not_found);

		setTitle(artist);

		if (savedInstanceState != null)

			mContent =
					(BaseArtistFragment) getSupportFragmentManager().getFragment(savedInstanceState,
							BUNDLE_FRAGMENT);

		if (mContent == null)
		{
			mContent = BaseArtistFragment.newInstance
					(ArtistInfo.IMAGES);
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

            if (mContent != null)
                    getSupportFragmentManager().putFragment(outState, BUNDLE_FRAGMENT,
                                    mContent);

    }

	/*
	 * private void saveCurrentFrag() { //FragmentManager mgr =
	 * getSupportFragmentManager();
	 * 
	 * //savedFrags.putParcelable(mContent.getType().toString(),
	 * //mgr.saveFragmentInstanceState(mContent)); }
	 */
	@Override
	public void onBackPressed()
	{
		FragmentManager mgr = getSupportFragmentManager();
		if (mgr.getBackStackEntryCount() > 0)
		{
			mgr.popBackStack();
		}
		else
		{
			finish();
		}
	}

	public void switchContent(ArtistInfo info)
	{
		// saveCurrentFrag();
		FragmentManager mgr = getSupportFragmentManager();

		// SavedState state = null;

		BaseArtistFragment newContent = BaseArtistFragment.newInstance(info);

		mgr.beginTransaction()
				.addToBackStack(mContent.getType().toString())
				.replace(R.id.fragment_frame, newContent)
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
