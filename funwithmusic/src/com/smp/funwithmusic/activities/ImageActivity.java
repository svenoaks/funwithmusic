package com.smp.funwithmusic.activities;

import static com.smp.funwithmusic.global.Constants.WEB_URL;
import uk.co.senab.photoview.PhotoViewAttacher;

import com.smp.funwithmusic.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageActivity extends Activity
{
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		imageView = (ImageView) findViewById(R.id.image);

		Intent intent = getIntent();
		String url = intent.getStringExtra(WEB_URL);
		 
		Picasso.with(this).load(url).into(imageView, new Callback()
		{

			@Override
			public void onError()
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess()
			{
				PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
			}
			
		});
		
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (isFinishing())
		{
			// Always cancel the request here, this is safe to call even if the
			// image has been loaded.
			// This ensures that the anonymous callback we have does not prevent
			// the activity from
			// being garbage collected. It also prevents our callback from
			// getting invoked even after the
			// activity has finished.
			Picasso.with(this).cancelRequest(imageView);
		}
	}

}
