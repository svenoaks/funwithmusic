package com.smp.funwithmusic.global;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

public class GlobalRequest
{
	private static GlobalRequest instance;
	private RequestQueue queue;
	private ImageLoader imageLoader;

	class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageCache
	{

		private final String TAG = this.getClass().getSimpleName();

		public BitmapLruImageCache(int maxSize)
		{
			super(maxSize);
		}

		@SuppressLint("NewApi")
		@Override
		protected int sizeOf(String key, Bitmap value)
		{

			if (Build.VERSION.SDK_INT >= 19)
			{
				return value.getAllocationByteCount();
			}

			return value.getRowBytes() * value.getHeight();
		}

		@Override
		public Bitmap getBitmap(String url)
		{
			Log.v(TAG, "Retrieved item from Mem Cache");
			return get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap)
		{
			Log.v(TAG, "Added item to Mem Cache");
			put(url, bitmap);
		}
	}

	private GlobalRequest(Context context)
	{
		// Gets the dimensions of the device's screen
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;

		// Assuming an ARGB_8888 pixel format, 4 bytes per pixel
		int size = screenWidth * screenHeight * 4;

		// 3 bitmaps to store therefore multiply bitmap size by 3
		final int cacheSize = size * 3;

		queue = Volley.newRequestQueue(context);
		BitmapLruImageCache mCache = new BitmapLruImageCache(cacheSize);
		imageLoader = new ImageLoader(queue, mCache);
	}

	public RequestQueue getRequestQueue()
	{
		return queue;
	}

	public ImageLoader getImageLoader()
	{
		return imageLoader;
	}

	public static synchronized GlobalRequest getInstance(Context context)
	{
		if (instance == null)
		{
			instance = new GlobalRequest(context.getApplicationContext());
		}

		return instance;
	}
}
