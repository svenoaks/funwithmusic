package com.smp.funwithmusic.services;

import static com.smp.funwithmusic.utilities.Constants.*;

import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

import com.smp.funwithmusic.receivers.SongReceiver;

import edu.gvsu.masl.echoprint.AudioFingerprinter;
import edu.gvsu.masl.echoprint.AudioFingerprinter.AudioFingerprinterListener;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class IdentifyMusicService extends IntentService implements AudioFingerprinterListener
{
	private static final int TIME_TO_LISTEN = 20;
	private volatile boolean successful;
	private CountDownLatch latch;
	
	private String artist;
	private String album;
	private String title;

	public IdentifyMusicService()
	{
		super("identify");
	}
	
	private void sendIntent()
	{
		Intent send = new Intent(this, SongReceiver.class);
		send.setAction(ID_ACTION);
				//.addCategory(Intent.CATEGORY_DEFAULT);
		send.putExtra("artist", artist);
		send.putExtra("title", title);
		send.putExtra("album", album);
		send.putExtra(FROM_ID, true);
		sendBroadcast(send);	
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
		
		sendIntent();
	}
	
	@Override
	public void didGenerateFingerprintCode(String code)
	{
		if (code != null && code.length() != 0)
		{
			
		}
		
	}
	
	
	
	
	
	
	
	
	//useless
	@Override
	public void didFailWithException(Exception e)
	{
		//latch.countDown();
	}
	@Override
	public void didFinishListening()
	{
		
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
