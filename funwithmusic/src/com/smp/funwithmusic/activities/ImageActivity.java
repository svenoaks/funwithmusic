package com.smp.funwithmusic.activities;

import static com.smp.funwithmusic.utilities.Constants.WEB_URL;
import uk.co.senab.photoview.PhotoViewAttacher;

import com.smp.funwithmusic.R;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		Intent intent = getIntent();
		String url = intent.getStringExtra(WEB_URL);
		
		ImageView imageView = (ImageView) findViewById(R.id.image);
		Picasso.with(this).load(url).into(imageView);
		new PhotoViewAttacher(imageView);
	}

}
