package com.smp.funwithmusic.activities;

import com.smp.funwithmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpActivity extends Activity
{

	private static final int FIRST_CARD = 1;
	private static final int SECOND_CARD = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		String musicPlayerTitle = getResources().getString(R.string.help_title1);
		String musicPlayerText = getResources().getString(R.string.help_text1);
		
		String idTitle = getResources().getString(R.string.help_title2);
		String idText = getResources().getString(R.string.help_text2);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		
		ViewGroup card1 = (ViewGroup) layout.getChildAt(FIRST_CARD);
		ViewGroup card2 = (ViewGroup) layout.getChildAt(SECOND_CARD);
		
		TextView title1 = (TextView) (card1.findViewById(android.R.id.title));
		TextView text1 = (TextView) (card1.findViewById(android.R.id.content));
		
		title1.setText(musicPlayerTitle);
		text1.setText(musicPlayerText);
		
		TextView title2 = (TextView) (card2.findViewById(android.R.id.title));
		TextView text2 = (TextView) (card2.findViewById(android.R.id.content));
		
		title2.setText(idTitle);
		text2.setText(idText);
		
	}

}
