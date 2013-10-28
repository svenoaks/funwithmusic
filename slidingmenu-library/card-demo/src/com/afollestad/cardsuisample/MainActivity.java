package com.afollestad.cardsuisample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.afollestad.cardsui.*;

public class MainActivity extends Activity implements Card.CardMenuListener<Card> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // This is quick way of theming the action bar without using styles.xml (e.g. using ActionBar Style Generator)
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_blue_dark)));
        getActionBar().setDisplayShowHomeEnabled(false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        // Initializes a CardAdapter with a blue accent color and basic popup menu for each card
        CardAdapter<Card> cardsAdapter = new CardAdapter<Card>(this)
                .setAccentColorRes(android.R.color.holo_blue_light)
                .setPopupMenu(R.menu.card_popup, this); // the popup menu callback is this activity

        CardListView cardsList = (CardListView) findViewById(R.id.cardsList);
        cardsList.setAdapter(cardsAdapter);
        cardsList.setOnCardClickListener(new CardListView.CardClickListener() {
            @Override
            public void onCardClick(int index, CardBase card, View view) {
                if (index == 0) {
                    startActivity(new Intent(MainActivity.this, CustomActivity.class));
                }
            }
        });

        cardsAdapter.add(new Card("View a custom adapter's cards")
                .setPopupMenu(-1, null)); // disables the popup menu set to the adapter for this card
        cardsAdapter.add(new CardHeader("Week Days"));
        cardsAdapter.add(new Card("Monday", "Back to work :("));
        cardsAdapter.add(new Card("Tuesday", "Arguably the worst day of the week."));
        cardsAdapter.add(new Card("Wednesday", "Hump day!"));
        cardsAdapter.add(new Card("Thursday", "Almost there..."));
        cardsAdapter.add(new Card("Friday", "We made it!"));

        cardsAdapter.add(new CardHeader("Companies", "The world's top tech businesses.")
                // The action text here is set to a string resource, if you don't specify a context and/or string the default "See More" is used
                .setAction(this, R.string.what_else, new CardHeader.ActionListener() {
                    @Override
                    public void onClick(CardHeader header) {
                        Toast.makeText(getApplicationContext(), header.getActionTitle(), Toast.LENGTH_SHORT).show();
                    }
                }));
        cardsAdapter.add(new Card("Google", "Android is the best!")
                .setThumbnail(this, R.drawable.android)  // sets a thumbnail image from drawable resources
                .setPopupMenu(-1, null));
        cardsAdapter.add(new Card("Microsoft", "We're trying.")
                .setThumbnail(this, R.drawable.wp)
                .setPopupMenu(-1, null));
        cardsAdapter.add(new Card("Apple", "We added a finger print scanner, give us your money.")
                .setThumbnail(this, R.drawable.ios)
                .setPopupMenu(-1, null));
    }

    @Override
    public void onMenuItemClick(Card card, MenuItem item) {
        Toast.makeText(this, card.getTitle() + ": " + item.getTitle(), Toast.LENGTH_SHORT).show();
    }
}