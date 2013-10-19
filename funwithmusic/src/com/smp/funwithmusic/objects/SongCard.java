package com.smp.funwithmusic.objects;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.afollestad.cardsui.Card;
import com.afollestad.silk.images.Dimension;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.images.SilkImageManager.ImageListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.apiclient.*;

public class SongCard extends Card implements ImageListener
{
	private static final long serialVersionUID = 889236084780166774L;

	private Song song;
	private Context context;

	public Song getSong()
	{
		return song;
	}
	
	
	public SongCard(final Song song, final Context context, final SilkImageManager mgr)
	{
		super(song.getTitle(), song.getAlbum());
		this.song = song;
		this.context = context;
		ItunesClient.get(song.getAlbum(), new JsonHttpResponseHandler() {
		    public void onSuccess(JSONObject obj) {
		       String url = ItunesClient.getImageUrl(obj, song.getArtist());    
		       if (url != null) mgr.get(url, SongCard.this, new Dimension(context, 56), false);
		       
		    }
		});
	}


	@Override
	public void onImageReceived(String source, Bitmap bitmap)
	{
		setThumbnail(new BitmapDrawable(context.getResources(), bitmap));
	}
}
