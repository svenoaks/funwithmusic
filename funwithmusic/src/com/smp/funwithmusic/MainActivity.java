package com.smp.funwithmusic;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onSteveClicked(View view)
	{
		startActivity(new Intent(this, SteveActivity.class));
	}
	
	public void onJohnClicked(View view)
	{
		startActivity(new Intent(this, JohnActivity.class));
	}

}
