package com.smp.funwithmusic.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

public class Performance implements Parcelable
{
	public String getDisplayName()
	{
		return displayName;
	}

	public int getBillingIndex()
	{
		return billingIndex;
	}

	public String getBilling()
	{
		return billing;
	}
	
	private String displayName;
	private int billingIndex;
	private String billing;

	public static class Builder
	{
		private String displayName;
		private int billingIndex;
		private String billing;
		
		public Builder(String displayName)
		{
			this.displayName = displayName;
		}
		
		public Builder billingIndex(int billingIndex)
		{
			this.billingIndex = billingIndex;
			return this;
		}

		public Builder billing(String billing)
		{
			this.billing = billing;
			return this;
		}

		public Performance build()
		{
			return new Performance(this);
		}
	}

	private Performance(Builder builder)
	{
		this.displayName = builder.displayName;
		this.billingIndex = builder.billingIndex;
		this.billing = builder.billing;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	public static final Parcelable.Creator<Performance> CREATOR = new Parcelable.Creator<Performance>()
	{
		public Performance createFromParcel(Parcel in)
		{
			return new Performance(in);
		}

		public Performance[] newArray(int size)
		{
			return new Performance[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(displayName);
		out.writeInt(billingIndex);
		out.writeString(billing);
	}

	private Performance(Parcel in)
	{
		displayName = in.readString();
		billingIndex = in.readInt();
		billing = in.readString();
	}
}
