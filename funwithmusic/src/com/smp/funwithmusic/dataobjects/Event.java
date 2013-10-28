package com.smp.funwithmusic.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable
{
	public String getType()
	{
		return type;
	}

	public Uri getMainUri()
	{
		return mainUri;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public String getStart()
	{
		return start;
	}

	public List<Performance> getPerformances()
	{
		return performances;
	}

	public String getLocation()
	{
		return location;
	}

	public String getVenueDisplayName()
	{
		return venueDisplayName;
	}

	public Uri getVenueUri()
	{
		return venueUri;
	}
	
	private String type;
	private Uri mainUri;
	private String displayName;
	private String start;
	private List<Performance> performances;
	private String location;
	private String venueDisplayName;
	private Uri venueUri;

	public static class Builder
	{
		private String type;
		private Uri mainUri;
		private String displayName;
		private String start;
		private List<Performance> performances;
		private String location;
		private String venueDisplayName;
		private Uri venueUri;

		public Builder (String displayName)
		{
			this.displayName = displayName;
		}
		
		public Builder type(String type)
		{
			this.type = type;
			return this;
		}

		public Builder mainUri(Uri mainUri)
		{
			this.mainUri = mainUri;
			return this;
		}

		public Builder start(String start)
		{
			this.start = start;
			return this;
		}

		public Builder performances(List<Performance> performances)
		{
			this.performances = performances;
			return this;
		}

		public Builder location(String location)
		{
			this.location = location;
			return this;
		}

		public Builder venueDisplayName(String venueDisplayName)
		{
			this.venueDisplayName = venueDisplayName;
			return this;
		}

		public Builder venueUri(Uri venueUri)
		{
			this.venueUri = venueUri;
			return this;
		}

		public Event build()
		{
			return new Event(this);
		}
	}

	private Event(Builder builder)
	{
		this.type = builder.type;
		this.mainUri = builder.mainUri;
		this.displayName = builder.displayName;
		this.start = builder.start;
		this.performances = builder.performances;
		this.location = builder.location;
		this.venueDisplayName = builder.venueDisplayName;
		this.venueUri = builder.venueUri;
	}

	public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>()
	{
		public Event createFromParcel(Parcel in)
		{
			return new Event(in);
		}

		public Event[] newArray(int size)
		{
			return new Event[size];
		}
	};
	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(type);
		out.writeParcelable(mainUri, 0);
		out.writeString(displayName);
		out.writeString(start);
		out.writeList(performances);
		out.writeString(location);
		out.writeString(venueDisplayName);
		out.writeParcelable(venueUri, 0);	
	}

	private Event(Parcel in)
	{
		type = in.readString();
		mainUri = in.readParcelable(null);
		displayName = in.readString();
		start = in.readString();
		performances = new ArrayList<Performance>();
		in.readList(performances, Performance.class.getClassLoader());
		location = in.readString();
		venueDisplayName = in.readString();
		venueUri = in.readParcelable(null);
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
