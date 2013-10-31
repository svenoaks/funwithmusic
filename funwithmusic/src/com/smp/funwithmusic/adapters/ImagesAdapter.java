package com.smp.funwithmusic.adapters;

import java.util.List;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.util.Log;
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
		ImageView imageView;
		if (convertView == null)
		{ // if it's not recycled, initialize some attributes
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		}
		else
		{
			imageView = (ImageView) convertView;
		}

		Picasso.with(context).load(urls.get(position))
				.resize(90, 90)
				.centerCrop()
				.into(imageView);
		Log.d("Images", "In get view  " + urls.get(position));
		return imageView;
	}

}
