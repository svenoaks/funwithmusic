package com.smp.funwithmusic.adapters;

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
	private int width, height;

	public ImagesAdapter(Context context, List<String> urls, int width, int height)
	{
		this.context = context;
		this.urls = urls;
		this.width = width;
		this.height = height;
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
		{ // if it's not recycled, initialize some attributes
			imageView = new ArtistImageView(context);
			// imageView.setLayoutParams(new GridView.LayoutParams(width,
			// height));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(0, 0, 0, 0);
		}
		else
		{
			imageView = (ArtistImageView) convertView;
		}

		Picasso.with(context).load(urls.get(position))
				.resize(width, height)
				.centerCrop()
				.placeholder(R.drawable.placeholder)
				.error(R.drawable.placeholder)
				.into(imageView);
		// Log.d("Images", "In get view  " + urls.get(position));
		return imageView;
	}
}
