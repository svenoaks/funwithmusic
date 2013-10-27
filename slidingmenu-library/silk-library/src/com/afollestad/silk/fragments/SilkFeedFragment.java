package com.afollestad.silk.fragments;

import android.os.Bundle;
import android.view.View;
import com.afollestad.silk.caching.SilkComparable;
import com.afollestad.silk.views.list.SilkListView;

import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class SilkFeedFragment<ItemType extends SilkComparable> extends SilkListFragment<ItemType> {

    protected boolean mInitialLoadOnResume;
    private boolean mBlockPaginate = false;

    @Override
    public void onResume() {
        super.onResume();
        if (mInitialLoadOnResume)
            onInitialRefresh();
    }

    protected void onPreLoad() {
    }

    protected void onPostLoad(List<ItemType> results, boolean paginated) {
        if (paginated) {
            getAdapter().add(results);
        } else {
            getAdapter().set(results);
        }
        setLoadComplete(false);
    }

    protected abstract List<ItemType> refresh() throws Exception;

    protected abstract List<ItemType> paginate() throws Exception;

    protected abstract void onError(Exception e);

    public void performRefresh(boolean showProgress) {
        if (isLoading()) return;
        setLoading(showProgress);
        onPreLoad();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<ItemType> items = refresh();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onPostLoad(items, false);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onError(e);
                            setLoadComplete(true);
                        }
                    });
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    public void performPaginate(boolean showProgress) {
        if (isLoading()) return;
        else if (mBlockPaginate) return;
        setLoading(showProgress);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<ItemType> items = paginate();
                    if (items == null || items.size() == 0) {
                        mBlockPaginate = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLoadComplete(false);
                            }
                        });
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int beforeCount = getAdapter().getCount();
                            onPostLoad(items, true);
                            getListView().smoothScrollToPosition(beforeCount);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onError(e);
                            setLoadComplete(true);
                        }
                    });
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    protected void onInitialRefresh() {
        getAdapter().clear();
        performRefresh(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getListView() instanceof SilkListView) {
            ((SilkListView) getListView()).setOnSilkScrollListener(new SilkListView.OnSilkScrollListener() {
                @Override
                public void onScrollToTop() {
                }

                @Override
                public void onScrollToBottom() {
                    performPaginate(false);
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mInitialLoadOnResume) onInitialRefresh();
    }
}