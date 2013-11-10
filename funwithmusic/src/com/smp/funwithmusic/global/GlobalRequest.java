package com.smp.funwithmusic.global;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class GlobalRequest
{
	private static Context context;
	private static RequestQueue queue;

	public static void init(Context context)
	{
		GlobalRequest.context = context.getApplicationContext();
	}
	
	public static synchronized RequestQueue getInstance()
	{
		if (queue == null)
		{
			queue = Volley.newRequestQueue(context);
		}
		
		return queue;
	}
}
