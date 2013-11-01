package com.smp.funwithmusic.dataobjects;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.smp.funwithmusic.utilities.Constants.*;

public class Song implements Serializable
{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 6925090966913539997L;
	private static Pattern featuring = Pattern.compile("\\s*\\(?[Ff]eat(\\.|uring).+\\z");

	private String title;
	private String artist;
	private String album;
	private String albumUrl;
	private String shortLyrics;
	private String fullLyricsUrl;
	private boolean cantGetAlbumUrl;
	private boolean cantGetLyrics;
	private boolean canAddLyrics;
	private boolean lyricsLoading;

	public boolean isCantGetLyrics()
	{
		return cantGetLyrics;
	}

	public void setCantGetLyrics(boolean cantGetLyrics)
	{
		this.cantGetLyrics = cantGetLyrics;
	}

	public String getFullLyricsUrl()
	{
		return fullLyricsUrl;
	}

	public void setFullLyricsUrl(String fullLyricsUrl)
	{
		this.fullLyricsUrl = fullLyricsUrl;
	}

	public String getShortLyrics()
	{
		return shortLyrics;
	}

	public void setShortLyrics(String shortLyrics)
	{
		this.shortLyrics = shortLyrics;
	}

	public boolean isCantGetAlbumUrl()
	{
		return cantGetAlbumUrl;
	}

	public void setCantGetAlbumUrl(boolean cantGetAlbumUrl)
	{
		this.cantGetAlbumUrl = cantGetAlbumUrl;
	}

	public String getAlbumUrl()
	{
		return albumUrl;
	}

	public void setAlbumUrl(String albumUrl)
	{
		this.albumUrl = albumUrl;
	}

	public Song(String title, String artist, String album)
	{
		this.title = title;
		this.artist = artist;
		this.album = album;
	}
	public Song()
	{
		
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

	public boolean hasAlbumUrl()
	{
		return !(albumUrl == null);
	}

	public boolean hasLyrics()
	{
		return !(shortLyrics == null || fullLyricsUrl == null);
	}

	public void resetLyrics()
	{
		if (shortLyrics == null || shortLyrics.equals(NOT_FOUND_WITH_ADD))
		{
			shortLyrics = fullLyricsUrl = null;
			cantGetLyrics = false;
			canAddLyrics = false;
		}
	}

	public void resetImageUrl()
	{
		if (albumUrl == null)
			cantGetAlbumUrl = false;
	}
	
	//Returns whether this Song is a valid song that should be added to the list.
	//A song is not aware of the list it is being added to, so further validation
	//must be handled by the caller.
	
	public boolean validate()
	{
		return artist !=null && title != null && album != null &&
				! title.equals("Advertisement");
	}
	public void removeFeaturing()
	{
		artist = featuring.matcher(artist).replaceAll("");
	}

	public void setCanAddLyrics(boolean b)
	{
		canAddLyrics = b;
		
	}
	public boolean isCanAddLyrics()
	{
		return canAddLyrics;
		
	}

	public boolean isLyricsLoading()
	{
		return lyricsLoading;
	}

	public void setLyricsLoading(boolean lyricsLoading)
	{
		this.lyricsLoading = lyricsLoading;
	}
}
