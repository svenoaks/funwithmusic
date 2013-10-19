package com.smp.funwithmusic.objects;

import java.io.Serializable;

public class Song implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6925090966913539997L;
	
	String title;
	String artist;
	String album;
	
	public Song(String title, String artist, String album)
	{
		this.title = title;
		this.artist = artist;
		this.album = album;
	}
	@Override
	final public boolean equals(Object other)
	{
		if (other instanceof Song)
		{
			Song oSong = (Song) other;
			return this.title.equals(oSong.title) &&
				   this.artist.equals(oSong.artist) &&
				   this.album.equals(oSong.album);
					
		}
		return false;
	}
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getArtist()
	{
		return artist;
	}

	public void setArtist(String artist)
	{
		this.artist = artist;
	}

	public String getAlbum()
	{
		return album;
	}

	public void setAlbum(String album)
	{
		this.album = album;
	}
}
