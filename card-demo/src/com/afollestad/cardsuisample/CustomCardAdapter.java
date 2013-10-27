package com.afollestad.cardsuisample;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.views.image.SilkImageView;

public class CustomCardAdapter extends CardAdapter<Card> {

    private SilkImageManager mImageLoader;

    public CustomCardAdapter(Context context) {
        super(context, R.layout.card_larger); // the custom card layout is passed to the super constructor instead of every individual card
        setAccentColorRes(android.R.color.holo_red_dark);
        mImageLoader = new SilkImageManager(context);
    }

    @Override
    protected boolean onProcessThumbnail(ImageView icon, Card card, ViewGroup parent) {
        // Optional, you can modify properties of the icon ImageView here.
        // In this case, this view is a SilkImageView in the card_larger.xml layout.
        SilkImageView silkIcon = (SilkImageView) icon;
        if (getScrollState() == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            // If the list is being scrolled quickly, don't load the thumbnail (scroll state is reported from CardListView because it extends SilkListView)
            silkIcon.setImageDrawable(null);
        } else {
            silkIcon.setImageURL(mImageLoader, "http://cdn.crackberry.com/sites/crackberry.com/files/styles/large/public/topic_images/2013/ANDROID.png");
        }
        return true;
    }

    @Override
    protected boolean onProcessTitle(TextView title, Card card, int accentColor) {
        // Optional, you can modify properties of the title textview here.
        return super.onProcessTitle(title, card, accentColor);
    }

    @Override
    protected boolean onProcessContent(TextView content, Card card) {
        // Optional, you can modify properties of the content textview here.
        return super.onProcessContent(content, card);
    }

    @Override
    public View onViewCreated(int index, View recycled, Card item, ViewGroup parent) {
        // Optional, you can modify properties of other views that you add to the card layout that aren't the icon, title, content...
        return super.onViewCreated(index, recycled, item, parent);
    }
}