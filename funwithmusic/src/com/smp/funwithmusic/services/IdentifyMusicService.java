package com.smp.funwithmusic.services;

import static com.smp.funwithmusic.utilities.Constants.*;

import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;

import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNCoverArt;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.activities.FlowActivity;
import com.smp.funwithmusic.apiclient.EchoNestClient;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.receivers.SongReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class IdentifyMusicService extends IntentService 
{
	private static final int TIME_TO_LISTEN = 20;

	private CountDownLatch latch;
	private volatile boolean successful;
	private volatile String artist;
	private volatile String album;
	private volatile String title;

	private GNConfig config;

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

	private class RecognizeFromMic implements GNSearchResultReady
	{
		void doFingerprint()
		{
			GNOperations.recognizeMIDStreamFromMic(this, config);
		}

		public void GNResultReady(GNSearchResult result)
		{

			if (result.isFingerprintSearchNoMatchStatus())
			{
				latch.countDown();
			}
			else
			{
				successful = true;
				GNSearchResponse response = result.getBestResponse();
				Intent send = new Intent(IdentifyMusicService.this, SongReceiver.class);
				send.setAction(ACTION_ID);
				// .addCategory(Intent.CATEGORY_DEFAULT);
				artist = response.getArtist();
				title = response.getTrackTitle();
				album = response.getAlbumTitle();
				String imageUrl = null;
				GNCoverArt art = response.getCoverArt();
				if (art != null)
					imageUrl = art.getUrl();
				send.putExtra("artist", artist);
				send.putExtra("title", title);
				send.putExtra("album", album);
				send.putExtra("imageUrl", imageUrl);
				sendBroadcast(send);
				latch.countDown();
			}
		}
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		latch = new CountDownLatch(1);
		successful = false;
		config = GNConfig.init(API_KEY_GRACENOTE, this.getApplicationContext());
		config.setProperty("content.coverArt", "1");
		RecognizeFromMic task = new RecognizeFromMic();
		task.doFingerprint();
		// AudioFingerprinter fingerprinter = new AudioFingerprinter(this);
		// fingerprinter.fingerprint(TIME_TO_LISTEN);
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

	
	
	
	
	
	
	
	
	
	
	
	
	/*
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
*/
}
