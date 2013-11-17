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

public class SongCard extends Card
{
	private static final long serialVersionUID = 889236084780166774L;

	private Song song;

	public Song getSong()
	{
		return song;
	}

	public SongCard(final Song song, final Context context)
	{
		super(song.getTitle(), song.getAlbum());
		this.song = song;
	}
}
