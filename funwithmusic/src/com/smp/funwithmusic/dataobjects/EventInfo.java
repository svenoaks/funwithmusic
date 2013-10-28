package com.smp.funwithmusic.dataobjects;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class EventInfo implements Parcelable
{
	public String getArtist()
	{
		return artist;
	}

	public List<Event> getEventList()
	{
		return eventList;
	}
	
	private String artist;
	private List<Event> eventList;

	public static class Builder
	{
		private String artist;
		private List<Event> eventList;

		public Builder (String artist, List<Event> eventList)
		{
			this.artist = artist;
			this.eventList = eventList;
		}

		public EventInfo build()
		{
			return new EventInfo(this);
		}

	}

	private EventInfo(Builder builder)
	{
		this.artist = builder.artist;
		this.eventList = builder.eventList;
	}

	public static final Parcelable.Creator<EventInfo> CREATOR = new Parcelable.Creator<EventInfo>()
	{
		public EventInfo createFromParcel(Parcel in)
		{
			return new EventInfo(in);
		}

		public EventInfo[] newArray(int size)
		{
			return new EventInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(artist);
		out.writeList(eventList);
	}

	private EventInfo(Parcel in)
	{
		artist = in.readString();
		eventList = new ArrayList<Event>();
		in.readList(eventList, Event.class.getClassLoader());
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
