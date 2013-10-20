package com.smp.funwithmusic.activities;

import java.util.ArrayList;
import java.util.List;

import com.smp.funwithmusic.R;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventInfo;
import com.smp.funwithmusic.dataobjects.Performance;

import static com.smp.funwithmusic.utilities.Constants.*;
import static com.smp.funwithmusic.utilities.UtilityMethods.*;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Context;
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
		startActivity(new Intent(this, FlowActivity.class));
	}

	public void onJohnClicked(View view)
	{
		Performance testPerformance = new Performance.Builder("Wild Flag")
				.billing("headline")
				.billingIndex(1)
				.build();
		
		List<Performance> perfomances = new ArrayList<Performance>();
		perfomances.add(testPerformance);

		Event testEvent = new Event.Builder("Wild Flag at The Fillmore (April 18, 2012)")
				.type("Concert")
				.mainUri(Uri.parse("http://www.songkick.com/concerts/11129128-wild-flag-at-fillmore?utm_source=PARTNER_ID&utm_medium=partner"))
				.start("2012-04-18T20:00:00-0800")
				.performances(perfomances)
				.location("San Francisco, CA, US")
				.venueDisplayName("The Fillmore")
				.venueUri(Uri.parse("http://www.songkick.com/venues/6239-fillmore?utm_source=PARTNER_ID&utm_medium=partner"))
				.build();
		
		Event testEvent2 = new Event.Builder("Wild Flag at WHEREVER (April 18, 2012)")
		.type("Concert")
		.mainUri(Uri.parse("http://www.songkick.com/concerts/11129128-wild-flag-at-fillmore?utm_source=PARTNER_ID&utm_medium=partner"))
		.start("2012-04-18T20:00:00-0800")
		.performances(perfomances)
		.location("YADA, CA, US")
		.venueDisplayName("The Fillmore")
		.venueUri(Uri.parse("http://www.songkick.com/venues/6239-fillmore?utm_source=PARTNER_ID&utm_medium=partner"))
		.build();
		
		List<Event> events = new ArrayList<Event>();
		events.add(testEvent);
		events.add(testEvent2);
		
		EventInfo testInfo = new EventInfo.Builder("Wild Flag", events).build();
		
		startNewActivityWithObject(this, EventActivity.class, testInfo, EVENT_NAME);
	}

}
