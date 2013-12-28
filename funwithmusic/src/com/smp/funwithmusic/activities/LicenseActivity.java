package com.smp.funwithmusic.activities;

import com.smp.funwithmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class LicenseActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_license);
		
		TextView apacheLink = (TextView) findViewById(R.id.apache_link);
		TextView commonsLink = (TextView) findViewById(R.id.commons_link);
		
		apacheLink.setMovementMethod(LinkMovementMethod.getInstance());
		commonsLink.setMovementMethod(LinkMovementMethod.getInstance());
	}
}
