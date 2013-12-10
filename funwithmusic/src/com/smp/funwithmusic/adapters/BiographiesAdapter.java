package com.smp.funwithmusic.adapters;

import java.util.List;

import com.smp.funwithmusic.R;
import com.smp.funwithmusic.dataobjects.Biography;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
//unused
public class BiographiesAdapter extends BaseAdapter
{
	List<Biography> bios;
	Context context;
	LayoutInflater inflater;

	public BiographiesAdapter(Context context, List<Biography> objects)
	{
		this.context = context;
		bios = objects;
		inflater = (LayoutInflater) context.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.list_item_biographies, parent, false);
		}

		TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
		TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
		TextView text3 = (TextView) convertView.findViewById(R.id.text3);

		Biography bio = bios.get(position);

		text1.setText(bio.getSite());
		text2.setText(bio.getText());
		text3.setText(bio.getUrl());

		return convertView;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return bios.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
