package com.smp.funwithmusic.global;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class GlobalRequest
{
	private static GlobalRequest instance;
	private Context context;
	private RequestQueue queue;
	private ImageLoader imageLoader;

	

	private GlobalRequest(Context context)
	{
		 queue = Volley.newRequestQueue(context);
	        imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
	            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);
	            public void putBitmap(String url, Bitmap bitmap) {
	                mCache.put(url, bitmap);
	            }
	            public Bitmap getBitmap(String url) {
	                return mCache.get(url);
	            }
	        });
	}

	public RequestQueue getRequestQueue(){
        return queue;
    }
 
    public ImageLoader getImageLoader(){
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
