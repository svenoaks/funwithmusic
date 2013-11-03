package com.smp.funwithmusic.dataobjects;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Biography implements Parcelable
{
	String text;
	String site;
	String url;

	public static final Parcelable.Creator<Biography> CREATOR = new Parcelable.Creator<Biography>()
	{
		public Biography createFromParcel(Parcel in)
		{
			return new Biography(in);
		}

		public Biography[] newArray(int size)
		{
			return new Biography[size];
		}
	};

	private Biography(Parcel in)
	{
		text = in.readString();
		site = in.readString();
		url = in.readString();
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(text);
		out.writeString(site);
		out.writeString(url);
	}

	private Biography(Builder builder)
	{
		this.text = builder.text;
		this.site = builder.site;
		this.url = builder.url;
	}

	public static class Builder
	{
		private String text;
		private String site;
		private String url;

		public Builder withText(String text)
		{
			this.text = text;
			return this;
		}

		public Builder withSite(String site)
		{
			this.site = site;
			return this;
		}

		public Builder withUrl(String url)
		{
			this.url = url;
			return this;
		}

		public Biography build()
		{
			return new Biography(this);
		}

	}

	public String getText()
	{
		return text;
	}

	public String getSite()
	{
		return site;
	}

	public String getUrl()
	{
		return url;
	}
}
