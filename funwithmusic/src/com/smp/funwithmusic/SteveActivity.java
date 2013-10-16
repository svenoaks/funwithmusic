package com.smp.funwithmusic;

import static com.smp.funwithmusic.Constants.*;
import static com.smp.funwithmusic.UtilityMethods.*;

import java.util.ArrayList;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SteveActivity extends Activity
{
	CardUI mCardView;
	ArrayList<String> songs;
	private IntentFilter filter;
	private UpdateActivityReceiver receiver;

	private class UpdateActivityReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			addCardsFromList();
		}

	}
	@Override
	protected void onPause()
	{
		super.onPause();

		unregisterReceiver(receiver);
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		addCardsFromList();
		registerReceiver(receiver, filter);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steve);
		
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		
		filter = new IntentFilter(SONG_ACTION);
		filter.addCategory(Intent.CATEGORY_DEFAULT);

		receiver = new UpdateActivityReceiver();
		
		//TestCards();
	}
	private void addCardsFromList()
	{
		ArrayList<String> songs = getSongList(this);
		if (this.songs == null || this.songs.size() != songs.size())
		{
			mCardView.clearCards();
			for (String song : songs)
			{
				addCard(song);
			}
			mCardView.refresh();
		}
	}
	private void addCard(String title)
	{
		mCardView.addCard(new MusicCard(title));
		
	}
	private void TestCards()
	{
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		 
		// add AndroidViews Cards
		mCardView.addCard(new MusicCard("Get the CardsUI view"));
		mCardView.addCardToLastStack(new MusicCard("for Android at"));
		MusicCard androidViewsCard = new MusicCard("www.androidviews.net");
		androidViewsCard.setOnClickListener(new OnClickListener() {
		 
		            @Override
		            public void onClick(View v) {
		                Intent intent = new Intent(Intent.ACTION_VIEW);
		                intent.setData(Uri.parse("http://www.androidviews.net/"));
		                startActivity(intent);
		 
		            }
		        });
		mCardView.addCardToLastStack(androidViewsCard);
		 
		// add one card, and then add another one to the last stack.
		mCardView.addCard(new MusicCard("2 cards"));
		mCardView.addCardToLastStack(new MusicCard("2 cards"));
		 
		// add one card
		mCardView.addCard(new MusicCard("1 card"));
		 
		// create a stack
		CardStack stack = new CardStack();
		stack.setTitle("title test");
		 
		// add 3 cards to stack
		stack.add(new MusicCard("3 cards"));
		stack.add(new MusicCard("3 cards"));
		stack.add(new MusicCard("3 cards"));
		 
		// add stack to cardView
		mCardView.addStack(stack);
		 
		// draw cards
		mCardView.refresh();
	}
	
}
