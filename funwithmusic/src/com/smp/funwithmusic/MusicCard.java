package com.smp.funwithmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import static com.smp.funwithmusic.Constants.*;

import com.fima.cardsui.objects.Card;

public class MusicCard extends Card
{
	Song song;
	
	public Song getSong()
	{
		return song;
	}

	public MusicCard(Song song)
	{
		super(song.getTitle());
		this.song = song;
	}

	@Override
	public View getCardContent(Context context)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.card_song, null);

		((TextView) view.findViewById(R.id.title)).setText(title);

		return view;
	}
}
