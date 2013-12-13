package com.smp.funwithmusic.views;

import com.android.volley.toolbox.NetworkImageView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ArtistImageView extends NetworkImageView
{
	private static final float HEIGHT_MULTIPLIER = 1.5f;
	public ArtistImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ArtistImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ArtistImageView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() * HEIGHT_MULTIPLIER));

	}
}
