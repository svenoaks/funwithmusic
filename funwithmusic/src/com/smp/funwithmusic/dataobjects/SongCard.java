package com.smp.funwithmusic.dataobjects;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.afollestad.cardsui.Card;
import com.afollestad.silk.images.Dimension;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.images.SilkImageManager.ImageListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.smp.funwithmusic.R;
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

	public SongCard(final Song song, final Context context)
	{
		super(song.getTitle(), song.getAlbum());
		this.song = song;
		this.context = context;
		// setThumbnail(new BitmapDrawable(context.getResources(),
		// getAlbumart((long) getAlbumId(song))));
		//Drawable myIcon = context.getResources().getDrawable( R.drawable.assem );
		//setThumbnail(myIcon);
		/*
		ItunesClient.get(song.getAlbum(), new JsonHttpResponseHandler()
		{
			public void onSuccess(JSONObject obj)
			{
				String url =
						ItunesClient.getImageUrl(obj, song.getArtist());
				if (url != null)
				{
					mgr.get(url, SongCard.this, null, false);
					Log.i("URL", url);
				}

			}
		});
		*/
	}

	public Bitmap getAlbumart(Long album_id)
	{
		Bitmap bm = null;
		try
		{
			final Uri sArtworkUri = Uri
					.parse("content://media/external/audio/albumart");

			Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

			ParcelFileDescriptor pfd = context.getContentResolver()
					.openFileDescriptor(uri, "r");

			if (pfd != null)
			{
				FileDescriptor fd = pfd.getFileDescriptor();
				bm = BitmapFactory.decodeFileDescriptor(fd);
			}
		}
		catch (Exception e)
		{
		}
		return bm;
	}

	private int getAlbumId(Song song)
	{
		Cursor cur = ((Activity) context).getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		int albumId = -1;

		if (cur.moveToFirst())
		{
			do
			{
				int albumIdIndex = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
				int albumIndex = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
				int artistIndex = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
				String album = cur.getString(albumIndex);
				String artist = cur.getString(artistIndex);
				Log.i("URL", album + " " + artist);
				if (song.getAlbum().equals(album) && song.getArtist().equals(artist))
				{
					albumId = cur.getInt(albumIdIndex);
					// Log.i("URL", album + " " + artist);
					break;
				}

				// LogUtil.i(TAG, "albumid= " + albumId + ", album= " + name +
				// ", artist=" + artist);

			}
			while (cur.moveToNext());

		}

		return albumId;
	}

	@Override
	public void onImageReceived(String source, Bitmap bitmap)
	{
		//Drawable myIcon = context.getResources().getDrawable( R.drawable.assem );
		//setThumbnail(myIcon);
		setThumbnail(context, bitmap);
	}

}
