package com.afollestad.cardsui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A {@link ListView} that automates many card related things, such as disabling the background list selector,
 * removing the list divider, and calling a {@link CardHeader}'s ActionListener when tapped by the user.
 *
 * @author Aidan Follestad (afollestad)
 */
public class CardListView extends ListView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private CardClickListener mCardClickListener;
    private CardLongClickListener mCardLongClickListener;

    public CardListView(Context context) {
        super(context);
        init(null);
    }

    public CardListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CardListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setDivider(null);
        setDividerHeight(0);
        setSelector(android.R.color.transparent);
        super.setOnItemClickListener(this);
        super.setOnItemLongClickListener(this);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{android.R.attr.background});
            if (a.length() > 0) {
                int color = a.getColor(0, 0);
                if (color == 0) setDefaultBackground();
                else {
                    setBackgroundColor(color);
                    setCacheColorHint(color);
                }
            } else setDefaultBackground();
        } else setDefaultBackground();
    }

    private void setDefaultBackground() {
        int gray = getResources().getColor(R.color.card_gray);
        setBackgroundColor(gray);
        setCacheColorHint(gray);
    }

    /**
     * @deprecated Use {@link #setAdapter(CardAdapter)} instead.
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter instanceof CardAdapter) {
            setAdapter((CardAdapter) adapter);
            return;
        }
        throw new RuntimeException("The CardListView only accepts CardAdapters.");
    }

    /**
     * Sets the list's adapter, enforces the use of only a CardAdapter, not any other type of adapter
     */
    public void setAdapter(CardAdapter adapter) {
        super.setAdapter(adapter);
    }

    /**
     * @deprecated Use {@link #setOnCardClickListener(com.afollestad.cardsui.CardListView.CardClickListener)} instead.
     */
    @Override
    public final void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * @deprecated Use {@link #setOnCardLongClickListener(com.afollestad.cardsui.CardListView.CardLongClickListener)} instead.
     */
    @Override
    public final void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    /**
     * Sets a click listener for cards (doesn't include card headers).
     */
    public void setOnCardClickListener(CardClickListener listener) {
        mCardClickListener = listener;
    }

    /**
     * Sets a long click listener for cards (doesn't include card headers).
     */
    public void setOnCardLongClickListener(CardLongClickListener listener) {
        mCardLongClickListener = listener;
    }

    @Override
    public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CardBase item = (CardBase) ((CardAdapter) getAdapter()).getItem(position);
        if (item.isHeader()) {
            CardHeader header = (CardHeader) item;
            if (header.getActionCallback() != null)
                header.getActionCallback().onClick(header);
            else if (mItemClickListener != null) mItemClickListener.onItemClick(parent, view, position, id);
        } else {
            if (mItemClickListener != null) mItemClickListener.onItemClick(parent, view, position, id);
            if (mCardClickListener != null) mCardClickListener.onCardClick(position, item, view);
            if (mCardLongClickListener != null) mCardLongClickListener.onCardLongClick(position, item, view);
        }
    }

    @Override
    public final boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        CardBase item = (CardBase) ((CardAdapter) getAdapter()).getItem(position);
        if (mCardLongClickListener != null)
            return mCardLongClickListener.onCardLongClick(position, item, view);
        return mItemLongClickListener != null && mItemLongClickListener.onItemLongClick(parent, view, position, id);
    }

    public interface CardClickListener {
        public void onCardClick(int index, CardBase card, View view);
    }

    public interface CardLongClickListener {
        public boolean onCardLongClick(int index, CardBase card, View view);
    }
}
