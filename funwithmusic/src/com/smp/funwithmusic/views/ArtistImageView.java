package com.smp.funwithmusic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ArtistImageView extends ImageView
{
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
		setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() * 1.5));

	}
}
