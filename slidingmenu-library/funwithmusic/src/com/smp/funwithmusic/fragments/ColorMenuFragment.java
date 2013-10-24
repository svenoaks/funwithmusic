package com.smp.funwithmusic.fragments;

import com.smp.funwithmusic.R;
import com.smp.funwithmusic.activities.ArtistActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ColorMenuFragment extends ListFragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		String[] colors = {"Red", "Blue", "Green", "Yellow", "Purple" };
		ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1, colors);
		setListAdapter(colorAdapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id)
	{
		Fragment newContent = null;
		switch (position)
		{
			case 0:
				newContent = new ColorFragment(R.color.card_gray);
				break;
			case 1:
				newContent = new ColorFragment(R.color.card_pressed);
				break;
			case 2:
				newContent = new ColorFragment(R.color.light_blue);
				break;
			case 3:
				newContent = new ColorFragment(android.R.color.white);
				break;
			case 4:
				newContent = new ColorFragment(android.R.color.black);
				break;
		}
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
