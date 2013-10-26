package com.smp.funwithmusic.services;

import static com.smp.funwithmusic.utilities.Constants.*;

import com.smp.funwithmusic.receivers.SongReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class IdentifyMusicService extends IntentService
{


	

	public IdentifyMusicService()
	{
		super("identify");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Intent send = new Intent(this, SongReceiver.class);
		
		send.setAction(ID_ACTION);
				//.addCategory(Intent.CATEGORY_DEFAULT);
		send.putExtra("artist", "TEST");
		send.putExtra("track", "TESTtitle");
		send.putExtra("album", "albumtestttt");
		send.putExtra(FROM_ID, true);
		sendBroadcast(send);		
	}

}
