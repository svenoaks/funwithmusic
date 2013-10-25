package com.smp.funwithmusic.fragments;

import com.smp.funwithmusic.R;
import com.smp.funwithmusic.activities.ArtistActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ArtistMenuFragment extends ListFragment
{

	private static final int NO_OF_COLORS = 5;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.list_artist_info, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		String[] info = {"Events", "News", "Biographies", "Blogs", "Images", "Videos", "More" };
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(
	            getActivity(),android.R.layout.simple_list_item_1, info){

	        @Override
	        public View getView(int position, View convertView,
	                ViewGroup parent) {
	            View view =super.getView(position, convertView, parent);

	            TextView textView=(TextView) view.findViewById(android.R.id.text1);

	            switch (position % NO_OF_COLORS)
	            {
	            	case 0:
	            		textView.setTextColor(getResources().getColor(R.color.info_0));
	            		break;
	            	case 1:
	            		textView.setTextColor(getResources().getColor(R.color.info_1));
	            		break;
	            	case 2:
	            		textView.setTextColor(getResources().getColor(R.color.info_2));
	            		break;
	            	case 3:
	            		textView.setTextColor(getResources().getColor(R.color.info_3));
	            		break;
	            	case 4:
	            		textView.setTextColor(getResources().getColor(R.color.info_4));
	            		break;
	            }
	            

	            return view;
	        }
	    };
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id)
	{
		
		Bundle args = new Bundle();
		int color = 0;
		
		switch (position)
		{
			case 0:
				color = R.color.card_gray;
				
				break;
			case 1:
				color = R.color.card_pressed;
				
				break;
			case 2:
				color = R.color.light_blue;
				
				break;
			case 3:
				color = android.R.color.white;
				
				break;
			case 4:
				color = android.R.color.black;
				
				break;
		}
		
		Fragment newContent = ColorFragment.newInstance(color);
		if (newContent != null)
			switchFragment(newContent);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment)
	{
		if (getActivity() == null)
			return;

		ArtistActivity fca = (ArtistActivity) getActivity();
		fca.switchContent(fragment);

	}

}
