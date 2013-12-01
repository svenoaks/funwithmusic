package com.smp.funwithmusic.dataobjects;

import com.afollestad.cardsui.Card;

public class SongCard extends Card
{
	private static final long serialVersionUID = 889236084780166774L;

	private Song song;

	public Song getSong()
	{
		return song;
	}

	public SongCard(final Song song)
	{
		super(song.getTitle(), song.getAlbum());
		this.song = song;
	}
}
