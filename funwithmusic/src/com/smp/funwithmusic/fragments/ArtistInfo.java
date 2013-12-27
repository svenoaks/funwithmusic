package com.smp.funwithmusic.fragments;

public enum ArtistInfo
{
	EVENTS, NEWS, REVIEWS, BIOGRAPHIES, IMAGES;

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
			default:
				return "";
		}
	}
}