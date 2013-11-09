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
	public enum ArtistInfo
	{
		EVENTS, NEWS, REVIEWS, BIOGRAPHIES, IMAGES, VIDEOS, MORE;

		@Override
		public String toString()
		{
			switch (this)
			{
				case EVENTS:
					return "Events";
				case NEWS:
					return "News";
				case REVIEWS:
					return "Reviews";
				case BIOGRAPHIES:
					return "Biographies";
				case IMAGES:
					return "Images";
				case VIDEOS:
					return "Videos";
				case MORE:
					return "More";
				default:
					return "";
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.list_artist_info, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		ArrayAdapter<ArtistInfo> adapter = new ArtistMenuAdapter<ArtistInfo>(
				getActivity(), R.layout.list_item_artist_info, ArtistInfo.values());
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id)
	{
		if (getActivity() == null)
			return;

		ArtistActivity fca = (ArtistActivity) getActivity();
		fca.switchContent(ArtistInfo.values()[position]);
	}
}
