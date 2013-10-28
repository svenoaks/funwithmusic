package com.afollestad.silk.views.list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import com.afollestad.silk.adapters.SilkAdapter;

/**
 * A {@link android.widget.GridView} that connects to a {@link com.afollestad.silk.adapters.SilkAdapter} and notifies the adapter of its scroll state.
 * <p/>
 * When the GridView becomes idle (is no longer being scrolled or flinged), it notifies the adapter causing it to update.
 * <p/>
 * You can use getScrollState() from within a {@link com.afollestad.silk.adapters.SilkAdapter} to only load images when the ListView isn't scrolling.
 *
 * @author Aidan Follestad (afollestad)
 */
public class SilkGridView extends GridView {

    private int lastState;

    public SilkGridView(Context context) {
        super(context);
        init();
    }

    public SilkGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SilkGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastState == scrollState) return;
                lastState = scrollState;
                SilkAdapter adapter = (SilkAdapter) getAdapter();
                adapter.setScrollState(scrollState);
                if (scrollState == SCROLL_STATE_IDLE) {
                    // When the list is idle, notify the adapter to update (causing images to load)
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    /**
     * @deprecated Use {@link #setSilkAdapter(com.afollestad.silk.adapters.SilkAdapter)} instead.
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        throw new RuntimeException("Please use setSilkAdapter() instead of setAdapter() on the SilkListView.");
    }

    /**
     * Sets the list's adapter, enforces the use of only a SilkAdapter, not any other type of adapter
     */
    public final void setSilkAdapter(SilkAdapter adapter) {
        super.setAdapter(adapter);
    }
}