package com.smp.funwithmusic.fragments;

import com.smp.funwithmusic.R;
import com.smp.funwithmusic.activities.ArtistActivity;
import com.smp.funwithmusic.adapters.ArtistMenuAdapter;

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
	private enum info
	{
		Events, News, Biographies, Blogs, Images, Videos, More;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.list_menu_artist_info, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		ArrayAdapter<info> adapter = new ArtistMenuAdapter<info>(
				getActivity(), R.layout.list_item_artist_info, info.values());
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id)
	{

		Bundle args = new Bundle();
		int color = 0;
		Fragment newContent = null;
		switch (position)
		{
			case 0:
				color = R.color.card_gray;

				break;
			case 1:
				color = R.color.card_gray;

				break;
			case 2:
				newContent = BiographiesFragment.newInstance();
				break;
			case 3:
				color = R.color.card_gray;

				break;
			case 4:
				 newContent = ImagesFragment.newInstance();
				break;
			default:
				color = R.color.card_gray;
		}

		
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
