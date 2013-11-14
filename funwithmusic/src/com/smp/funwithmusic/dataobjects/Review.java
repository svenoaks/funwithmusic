package com.smp.funwithmusic.dataobjects;

public class Review
{
	private String name;
	private String url;
	private String summary;
	private String date;
	private String image_url;
	private String release;

	public String getName()
	{
		return name;
	}

	public String getUrl()
	{
		return url;
	}

	public String getSummary()
	{
		return summary;
	}

	public String getDate()
	{
		return date;
	}

	public String getImage_url()
	{
		return image_url;
	}

	public String getRelease()
	{
		return release;
	}

	private Review(Builder builder)
	{
		this.name = builder.name;
		this.url = builder.url;
		this.summary = builder.summary;
		this.date = builder.date;
		this.image_url = builder.image_url;
		this.release = builder.release;
	}

	public static class Builder
	{
		private String name;
		private String url;
		private String summary;
		private String date;
		private String image_url;
		private String release;

		public Builder withName(String name)
		{
			this.name = name;
			return this;
		}

		public Builder withUrl(String url)
		{
			this.url = url;
			return this;
		}

		public Builder withSummary(String summary)
		{
			this.summary = summary;
			return this;
		}

		public Builder withDate(String date)
		{
			this.date = date;
			return this;
		}

		public Builder withImage_url(String image_url)
		{
			this.image_url = image_url;
			return this;
		}

		public Builder withRelease(String release)
		{
			this.release = release;
			return this;
		}

		public Review build()
		{
			return new Review(this);
		}
	}
}
