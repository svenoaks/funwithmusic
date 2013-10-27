package com.smp.funwithmusic.services;

import static com.smp.funwithmusic.utilities.Constants.*;

import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.activities.FlowActivity;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.receivers.SongReceiver;

import edu.gvsu.masl.echoprint.AudioFingerprinter;
import edu.gvsu.masl.echoprint.AudioFingerprinter.AudioFingerprinterListener;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class IdentifyMusicService extends IntentService implements AudioFingerprinterListener
{
	private static final int TIME_TO_LISTEN = 20;
	private volatile boolean successful;
	private CountDownLatch latch;

	private volatile String artist;
	private volatile String album;
	private volatile String title;

	public IdentifyMusicService()
	{
		super("identify");
	}

	private void sendFinishedIntent()
	{
		Intent remove = new Intent(this, FlowActivity.class);
		remove.setAction(ACTION_REMOVE_IDENTIFY);
		if (artist != null & album != null && title != null)
		{
			remove.putExtra(LISTEN_SUCCESSFUL, true);
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(remove);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		successful = false;

		latch = new CountDownLatch(1);

		AudioFingerprinter fingerprinter = new AudioFingerprinter(this);
		fingerprinter.fingerprint(TIME_TO_LISTEN);
		try
		{
			latch.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		sendFinishedIntent();
	}

	@Override
	public void didGenerateFingerprintCode(String code)
	{
		if (code != null && code.length() != 0)
		{
			EchoNestClient.getIdentify(code, new JsonHttpResponseHandler()
			{
				@Override
				public void onSuccess(JSONObject json)
				{

					// Log.d("IDRESULT", result);

					Song result = EchoNestClient.parseIdentify(json);

					Intent send = new Intent(IdentifyMusicService.this, SongReceiver.class);
					send.setAction(ACTION_ID);
					// .addCategory(Intent.CATEGORY_DEFAULT);
					artist = result.getArtist();
					title = result.getTitle();
					album = "Fake Album";
					send.putExtra("artist", artist);
					send.putExtra("title", title);
					send.putExtra("album", album);
					sendBroadcast(send);

				}

				@Override
				public void onFailure(Throwable ex, String message)
				{

				}
			});
		}
		else
		{

		}

	}

	// useless
	@Override
	public void didFailWithException(Exception e)
	{
		// latch.countDown();
	}

	@Override
	public void didFinishListening()
	{
		latch.countDown();
	}

	@Override
	public void didFinishListeningPass()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void willStartListening()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void willStartListeningPass()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void didFindMatchForCode(Hashtable<String, String> table, String code)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void didNotFindMatchForCode(String code)
	{
		// TODO Auto-generated method stub

	}

}
