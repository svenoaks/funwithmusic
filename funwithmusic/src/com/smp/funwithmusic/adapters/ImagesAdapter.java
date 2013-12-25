package com.smp.funwithmusic.adapters;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.NetworkImageView;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.global.GlobalRequest;
import com.smp.funwithmusic.views.ArtistImageView;
import com.squareup.picasso.OkHttpDownloader;
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
	//private Picasso picasso;

	// int width, height;

	public ImagesAdapter(Context context, List<String> urls)
	{
		this.context = context;
		this.urls = urls;
		//refreshPicasso();
	}
	/*
	public void shutdownPicasso()
	{
		if (picasso != null)
			picasso.shutdown();
	}
	
	public void refreshPicasso()
	{
		Picasso.Builder builder = new Picasso.Builder(context);
		picasso = builder.downloader(new OkHttpDownloader(context)).build();
	}
	*/
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
		/*
		 imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		 imageView.setDefaultImageResId(R.drawable.placeholder);
		 imageView.setErrorImageResId(R.drawable.placeholder);
		 imageView.setImageUrl(urls.get(position), GlobalRequest
		 .getInstance(context).getImageLoader());
		 */
		GlobalRequest.getInstance(context)
		.getPicasso().load(urls.get(position))
				// .skipMemoryCache()
				.centerCrop()
				.placeholder(R.drawable.placeholder)
				.error(R.drawable.placeholder)
				.fit()
				// .noFade()
				.into(imageView);
		
		// Log.d("Images", "In get view  " + urls.get(position));
		return imageView;
	}
}
