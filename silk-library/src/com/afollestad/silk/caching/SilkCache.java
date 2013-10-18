package com.afollestad.silk.caching;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SilkCache<ItemType extends SilkComparable<ItemType>> extends SilkCacheBase<ItemType> {

    public SilkCache(Context context, String name, Class<?> cls, OnReadyCallback<ItemType> onReady) {
        super(context, name, cls);
        init(onReady);
    }

    public SilkCache(Context context, String name, Class<?> cls, OnReadyCallback<ItemType> onReady, Handler handler) {
        super(context, name, cls, handler);
        init(onReady);
    }

    private void init(final OnReadyCallback<ItemType> callback) {
        if (callback == null) {
            loadItems();
            return;
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadItems();
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onReady(SilkCache.this);
                    }
                });
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    public final SilkCache<ItemType> add(ItemType item) {
        add(0, item);
        return this;
    }

    public final SilkCache<ItemType> add(int index, ItemType item) {
        if (item == null) throw new IllegalArgumentException("You cannot add a null item.");
        log("Item " + item.getSilkId() + " added to the cache.");
        getBuffer().add(index, item);
        markChanged();
        return this;
    }

    public final SilkCache<ItemType> addAll(ItemType[] items) {
        addAll(0, new ArrayList<ItemType>(Arrays.asList(items)));
        return this;
    }

    public final SilkCache<ItemType> addAll(List<ItemType> items) {
        addAll(0, items);
        return this;
    }

    public final SilkCache<ItemType> addAll(int startIndex, ItemType[] items) {
        addAll(startIndex, new ArrayList<ItemType>(Arrays.asList(items)));
        return this;
    }

    public final SilkCache<ItemType> addAll(int startIndex, List<ItemType> items) {
        if (items == null || items.size() == 0)
            throw new IllegalArgumentException("You cannot add a null or empty collection.");
        markChanged();
        int index = startIndex;
        for (ItemType item : items) {
            getBuffer().add(index, item);
            index++;
        }
        log("Added " + items.size() + " items to the cache at index " + startIndex);
        return this;
    }

    public final SilkCache<ItemType> set(ItemType[] items) {
        set(new ArrayList<ItemType>(Arrays.asList(items)));
        return this;
    }

    public final SilkCache<ItemType> set(List<ItemType> items) {
        getBuffer().clear();
        getBuffer().addAll(items);
        log("Cache overwritten with " + items.size() + " items");
        markChanged();
        return this;
    }

    public final SilkCache<ItemType> update(ItemType item) {
        update(item, false);
        return this;
    }

    public final SilkCache<ItemType> update(ItemType item, boolean addIfNotFound) {
        if (item == null) throw new IllegalArgumentException("You cannot update a null item.");
        log("Updating item " + item.getSilkId().toString() + "...");
        boolean found = false;
        for (int i = 0; i < getBuffer().size(); i++) {
            if (item.equalTo(getBuffer().get(i))) {
                log("Item " + item.getSilkId() + " updated at index " + i);
                getBuffer().set(i, item);
                found = true;
                markChanged();
                break;
            }
        }
        if (!found) {
            if (addIfNotFound) add(item);
            else log("No item found in the cache for " + item.getSilkId());
        }
        return this;
    }

    public final ItemType find(ItemType item) {
        int index = findIndex(item);
        if (index == -1) return null;
        return getBuffer().get(index);
    }

    public final int findIndex(ItemType item) {
        if (item == null)
            throw new IllegalArgumentException("You cannot search for a null item.");
        log("Searching for " + item.getSilkId() + "...");
        for (int i = 0; i < getBuffer().size(); i++) {
            if (item.equalTo(getBuffer().get(i))) {
                log("Found " + item.getSilkId() + " at index " + i);
                return i;
            }
        }
        log("Item " + item.getSilkId() + " not found.");
        return -1;
    }

    public final void find(final ItemType item, final SimpleFindCallback<ItemType> callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int result = findIndex(item);
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                ItemType resultItem = null;
                                if (result > -1) resultItem = getBuffer().get(result);
                                callback.onFound(resultItem, result);
                            }
                        }
                    });
                } catch (final Exception e) {
                    log("Find error: " + e.getMessage());
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null && callback instanceof FindCallback)
                                ((FindCallback) callback).onError(e);
                        }
                    });
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    public final SilkCache<ItemType> remove(int index) {
        if (index == -1 || index > getBuffer().size() - 1)
            throw new IndexOutOfBoundsException("Index " + index + " is larger than the cache size (" + getBuffer().size() + ")");
        getBuffer().remove(index);
        markChanged();
        return this;
    }

    public final SilkCache<ItemType> remove(ItemType item) {
        if (item == null) throw new IllegalArgumentException("You cannot remove a null item.");
        int index = findIndex(item);
        if (index > -1) remove(index);
        return this;
    }

    public final SilkCache<ItemType> clear() {
        log("Removed " + getBuffer().size() + " items from the cache.");
        getBuffer().clear();
        markChanged();
        return this;
    }

    public final int size() {
        return getBuffer().size();
    }

    public final List<ItemType> read() {
        return getBuffer();
    }

    public interface SimpleCommitCallback {
        public void onError(Exception e);
    }

    public interface CommitCallback extends SimpleCommitCallback {
        public void onCommitted();
    }

    public interface SimpleFindCallback<ItemType> {
        public void onFound(ItemType item, int index);
    }

    public interface FindCallback<ItemType> extends SimpleFindCallback<ItemType> {
        public void onError(Exception e);
    }
}