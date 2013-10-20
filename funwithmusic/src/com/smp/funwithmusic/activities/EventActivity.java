package com.smp.funwithmusic.activities;

import java.util.List;

import com.smp.funwithmusic.R;
import com.smp.funwithmusic.R.layout;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventInfo;

import static com.smp.funwithmusic.utilities.Constants.*;
import static com.smp.funwithmusic.utilities.UtilityMethods.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class EventActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_john);
		
		Intent intent = getIntent();
		EventInfo eventInfo = intent.getParcelableExtra(EVENT_NAME);
		List<Event> events = eventInfo.getEventList();
		Toast.makeText(this, events.get(0).getDisplayName(), Toast.LENGTH_LONG).show();
		
	}
}
