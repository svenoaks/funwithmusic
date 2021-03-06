package com.smp.funwithmusic.services;

import static com.smp.funwithmusic.global.Constants.*;

import java.util.concurrent.CountDownLatch;

import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNCoverArt;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;
import com.smp.funwithmusic.receivers.SongReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class IdentifyMusicService extends IntentService
{
	private CountDownLatch latch;

	private volatile boolean successful;
	private volatile String artist;
	private volatile String album;
	private volatile String title;
	RecognizeFromMic task;

	private GNConfig config;

	public IdentifyMusicService()
	{
		super("identify");
	}

	@Override
	public void onDestroy()
	{
		GNOperations.cancel(task);
		if (successful)
		{
			Toast.makeText(this, TOAST_ID_SUCCESSFUL, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this, TOAST_ID_FAILURE, Toast.LENGTH_SHORT).show();
		}

		super.onDestroy();
	}

	private void sendFinishedIntent()
	{
		Intent remove = new Intent();
		remove.setAction(ACTION_REMOVE_IDENTIFY);

		remove.putExtra(EXTRA_LISTEN_SUCCESSFUL, successful);

		remove.addCategory(Intent.CATEGORY_DEFAULT);
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
				successful = false;
				latch.countDown();
			}
			else
			{

				GNSearchResponse response = result.getBestResponse();

				// .addCategory(Intent.CATEGORY_DEFAULT);

				if (response != null)
				{
					artist = response.getArtist();
					title = response.getTrackTitle();
					album = response.getAlbumTitle();
					String imageUrl = null;
					GNCoverArt art = response.getCoverArt();
					if (art != null)
						imageUrl = art.getUrl();

					Intent send = new Intent(IdentifyMusicService.this, SongReceiver.class);
					send.setAction(ACTION_ID);
					send.putExtra("artist", artist);
					send.putExtra("title", title);
					send.putExtra("album", album);
					send.putExtra("imageUrl", imageUrl);
					successful = true;
					sendBroadcast(send);
				}

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
		config.setProperty("content.coverArt.genreCoverArt", "1");
		config.setProperty("content.coverArt.sizePreference", "SMALL");
		task = new RecognizeFromMic();
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
	 * @Override public void didGenerateFingerprintCode(String code) { if (code
	 * != null && code.length() != 0) { EchoNestClient.getIdentify(code, new
	 * JsonHttpResponseHandler() {
	 * 
	 * @Override public void onSuccess(JSONObject json) {
	 * 
	 * // Log.d("IDRESULT", result);
	 * 
	 * Song result = EchoNestClient.parseIdentify(json);
	 * 
	 * Intent send = new Intent(IdentifyMusicService.this, SongReceiver.class);
	 * send.setAction(ACTION_ID); // .addCategory(Intent.CATEGORY_DEFAULT);
	 * artist = result.getArtist(); title = result.getTitle(); album =
	 * "Fake Album"; send.putExtra("artist", artist); send.putExtra("title",
	 * title); send.putExtra("album", album); sendBroadcast(send);
	 * 
	 * }
	 * 
	 * @Override public void onFailure(Throwable ex, String message) {
	 * 
	 * } }); } else {
	 * 
	 * }
	 * 
	 * }
	 * 
	 * // useless
	 * 
	 * @Override public void didFailWithException(Exception e) { //
	 * latch.countDown(); }
	 * 
	 * @Override public void didFinishListening() { latch.countDown(); }
	 * 
	 * @Override public void didFinishListeningPass() { // TODO Auto-generated
	 * method stub
	 * 
	 * }
	 * 
	 * @Override public void willStartListening() { // TODO Auto-generated
	 * method stub
	 * 
	 * }
	 * 
	 * @Override public void willStartListeningPass() { // TODO Auto-generated
	 * method stub
	 * 
	 * }
	 * 
	 * @Override public void didFindMatchForCode(Hashtable<String, String>
	 * table, String code) { // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void didNotFindMatchForCode(String code) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 */
}
