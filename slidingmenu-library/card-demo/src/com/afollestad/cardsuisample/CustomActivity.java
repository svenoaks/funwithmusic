package com.afollestad.cardsuisample;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardHeader;
import com.afollestad.cardsui.CardListView;

public class CustomActivity extends Activity implements Card.CardMenuListener<Card> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // This is quick way of theming the action bar without using styles.xml (e.g. using ActionBar Style Generator)
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_red_dark)));
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        // Initializes a CustomCardAdapter with a basic popup menu for each card
        // The adapter's accent color is set in its constructor, along with the custom card layout and image downloading logic
        CustomCardAdapter customAdapter = new CustomCardAdapter(this);
        customAdapter.setPopupMenu(R.menu.card_popup, this);

        CardListView cardsList = (CardListView) findViewById(R.id.cardsList);
        cardsList.setAdapter(customAdapter);

        customAdapter.add(new CardHeader("Custom Sample", "Using larger card layouts"));
        for (int i = 1; i <= 3; i++)
            customAdapter.add(new Card("Example #" + i, "Hello"));
    }

    @Override
    public void onMenuItemClick(Card card, MenuItem item) {
        Toast.makeText(this, card.getTitle() + ": " + item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}