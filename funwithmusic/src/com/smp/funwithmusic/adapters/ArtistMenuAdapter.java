package com.smp.funwithmusic.adapters;

import java.util.List;

import com.smp.funwithmusic.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArtistMenuAdapter<T> extends ArrayAdapter<T>
{
	Context context;

	public ArtistMenuAdapter(Context context, int resource, T[] info)
	{
		super(context, resource, info);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView,
			ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);

		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setTextColor(context.getResources().getColor(R.color.list_gray));
		/*
		 * switch (position) { case 0:
		 * textView.setTextColor(context.getResources
		 * ().getColor(R.color.info_0)); break; case 1:
		 * textView.setTextColor(context
		 * .getResources().getColor(R.color.info_1)); break; case 2:
		 * textView.setTextColor
		 * (context.getResources().getColor(R.color.info_2)); break; case 3:
		 * textView
		 * .setTextColor(context.getResources().getColor(R.color.info_3));
		 * break; case 4:
		 * textView.setTextColor(context.getResources().getColor(R
		 * .color.info_4)); break; case 5:
		 * textView.setTextColor(context.getResources
		 * ().getColor(R.color.info_0)); break; case 6:
		 * textView.setTextColor(context
		 * .getResources().getColor(R.color.info_1)); break; }
		 */
		return view;
	}

}
