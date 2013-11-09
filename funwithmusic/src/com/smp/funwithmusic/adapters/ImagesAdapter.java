package com.smp.funwithmusic.adapters;

import java.util.ArrayList;
import java.util.List;

import com.smp.funwithmusic.R;
import com.smp.funwithmusic.views.ArtistImageView;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImagesAdapter extends BaseAdapter
{
	private Context context;
	private List<String> urls;

	public ImagesAdapter(Context context, List<String> urls)
	{
		this.context = context;
		this.urls = urls;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return urls.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ArtistImageView imageView;
		if (convertView == null)
		{
			imageView = new ArtistImageView(context);
		}
		else
		{
			imageView = (ArtistImageView) convertView;
		}

		Picasso.with(context).load(urls.get(position))
				.centerCrop()
				.placeholder(R.drawable.placeholder)
				.error(R.drawable.placeholder)
				.fit()
				.into(imageView);
		// Log.d("Images", "In get view  " + urls.get(position));
		return imageView;
	}
}
