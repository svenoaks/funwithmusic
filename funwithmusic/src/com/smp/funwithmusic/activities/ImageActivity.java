package com.smp.funwithmusic.activities;

import static com.smp.funwithmusic.global.Constants.WEB_URL;
import uk.co.senab.photoview.PhotoViewAttacher;

import com.smp.funwithmusic.R;
import com.smp.funwithmusic.activities.ArtistActivity.DisplayedView;
import com.smp.funwithmusic.global.GlobalRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;

public class ImageActivity extends Activity
{
	@Override
	protected void onResume()
	{

		super.onResume();
		Intent intent = getIntent();
		String url = intent.getStringExtra(WEB_URL);

		flipper.setDisplayedChild(DisplayedView.LOADING.ordinal());

		GlobalRequest.getInstance(this).getPicasso()
				.load(url)
				.fit()
				.centerInside()
				.into(imageView, new Callback()
				{

					@Override
					public void onError()
					{
						flipper.setDisplayedChild(DisplayedView.NOT_FOUND.ordinal());
					}

					@Override
					public void onSuccess()
					{
						flipper.setDisplayedChild(DisplayedView.CONTENT.ordinal());
						PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
					}

				});
	}

	private ImageView imageView;
	private ViewFlipper flipper;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		imageView = (ImageView) findViewById(R.id.image);
		flipper = (ViewFlipper) findViewById(R.id.flipper);

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
			GlobalRequest.getInstance(this).getPicasso().cancelRequest(imageView);
		}
	}

}
