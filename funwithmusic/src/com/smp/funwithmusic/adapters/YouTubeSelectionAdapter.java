package com.smp.funwithmusic.adapters;

import java.util.List;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.dataobjects.Biography;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class YouTubeSelectionAdapter extends BaseAdapter
{
	List<SearchResult> vids;
	Context context;
	LayoutInflater inflater;

	public YouTubeSelectionAdapter(Context context, List<SearchResult> vids)
	{
		this.context = context;
		this.vids = vids;
		inflater = (LayoutInflater) context.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.list_item_youtube, parent, false);
		}

		TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
		ImageView image = (ImageView) convertView.findViewById(R.id.image);

		SearchResult vid = vids.get(position);

		Thumbnail thumbnail = (Thumbnail) vid.getSnippet().getThumbnails().getHigh();

		text1.setText(vid.getSnippet().getTitle());
		
		if (thumbnail != null)
		Picasso.with(context).load(thumbnail.getUrl())
				.centerInside()
				.fit()
				.into(image);

		return convertView;
	}

	@Override
	public int getCount()
	{
		return vids.size();
	}

	@Override
	public SearchResult getItem(int position)
	{
		return vids.get(position);
	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
