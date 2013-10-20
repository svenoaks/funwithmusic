package com.afollestad.silk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import com.afollestad.silk.caching.SilkComparable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A BaseAdapter wrapper that makes creating list adapters easier. Contains various convenience methods and handles
 * recycling views on its own.
 *
 * @param <ItemType> The type of items held in the adapter.
 * @author Aidan Follestad (afollestad)
 */
public abstract class SilkAdapter<ItemType extends SilkComparable> extends BaseAdapter {

    private final Context context;
    private final List<ItemType> items;
    private boolean isChanged = false;
    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    public SilkAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<ItemType>();
    }

    /**
     * Called to get the layout of a view being inflated by the SilkAdapter. The inheriting adapter class must return
     * the layout for list items, this should always be the same value unless you have multiple view types.
     * <p/>
     * If you override {#getItemViewType} and/or {#getViewTypeCount}, the parameter to this method will be filled with
     * the item type at the index of the item view being inflated. Otherwise, it can be ignored.
     */
    protected abstract int getLayout(int index, int type);

    /**
     * Called when a list item view is inflated and the inheriting adapter must fill in views in the inflated layout.
     * The second parameter ('recycled') should be returned at the end of the method.
     *
     * @param index    The index of the inflated view.
     * @param recycled The layout with views to be filled (e.g. text views).
     * @param item     The item at the current index of the adapter.
     * @param parent 
     */
    public abstract View onViewCreated(int index, View recycled, ItemType item, ViewGroup parent);

    /**
     * Gets the context passed in the constructor, that's used for inflating views.
     */
    protected final Context getContext() {
        return context;
    }

    /**
     * Adds an item at a specific index inside the adapter.
     */
    public void add(int index, ItemType toAdd) {
        isChanged = true;
        this.items.add(index, toAdd);
        notifyDataSetChanged();
    }

    /**
     * Adds a list of items to the adapter and notifies the attached Listview.
     *
     * @param index The index to begin adding items at (inserted starting here).
     * @param toAdd The items to add.
     */
    public final void add(int index, List<ItemType> toAdd) {
        for (ItemType aToAdd : toAdd) {
            add(index, aToAdd);
            index++;
        }
    }

    /**
     * Adds a single item to the adapter and notifies the attached ListView.
     */
    public void add(ItemType toAdd) {
        isChanged = true;
        this.items.add(toAdd);
        notifyDataSetChanged();
    }

    /**
     * Adds an array of items to the adapter and notifies the attached ListView.
     */
    public final void add(ItemType[] toAdd) {
        add(new ArrayList<ItemType>(Arrays.asList(toAdd)));
    }

    /**
     * Adds a list of items to the adapter and notifies the attached Listview.
     */
    public final void add(List<ItemType> toAdd) {
        isChanged = true;
        for (ItemType item : toAdd)
            add(item);
    }

    /**
     * Updates a single item in the adapter using isSame() from SilkComparable. Once the filter finds the item, the loop is broken
     * so you cannot update multiple items with a single call.
     * <p/>
     * If the item is not found, it will be added to the adapter.
     *
     * @return True if the item was updated.
     */
    public boolean update(ItemType toUpdate) {
        return update(toUpdate, true);
    }

    /**
     * Updates a single item in the adapter using isSame() from SilkComparable. Once the filter finds the item, the loop is broken
     * so you cannot update multiple items with a single call.
     *
     * @param addIfNotFound Whether or not the item will be added if it's not found.
     * @return True if the item was updated or added.
     */
    public boolean update(ItemType toUpdate, boolean addIfNotFound) {
        boolean found = false;
        for (int i = 0; i < items.size(); i++) {
            if (toUpdate.equalTo(items.get(i))) {
                items.set(i, toUpdate);
                found = true;
                break;
            }
        }
        if (found) return true;
        else if (addIfNotFound) {
            add(toUpdate);
            return true;
        }
        return false;
    }

    /**
     * Sets the items in the adapter (clears any previous ones before adding) and notifies the attached ListView.
     */
    public final void set(ItemType[] toSet) {
        set(new ArrayList<ItemType>(Arrays.asList(toSet)));
    }

    /**
     * Sets the items in the adapter (clears any previous ones before adding) and notifies the attached ListView.
     */
    public final void set(List<ItemType> toSet) {
        isChanged = true;
        clear();
        for (ItemType item : toSet)
            add(item);
    }

    /**
     * Checks whether or not the adapter contains an item based on the adapter's inherited Filter.
     */
    public final boolean contains(ItemType item) {
        for (int i = 0; i < getCount(); i++) {
            ItemType curItem = getItem(i);
            if (item.equalTo(curItem)) return true;
        }
        return false;
    }

    /**
     * Removes an item from the list by its index.
     */
    public void remove(int index) {
        isChanged = true;
        this.items.remove(index);
        notifyDataSetChanged();
    }

    /**
     * Removes a single item in the adapter using isSame() from SilkComparable. Once the filter finds the item, the loop is broken
     * so you cannot remove multiple items with a single call.
     */
    public void remove(ItemType toRemove) {
        for (int i = 0; i < items.size(); i++) {
            if (toRemove.equalTo(items.get(i))) {
                this.remove(i);
                break;
            }
        }
    }

    /**
     * Removes an array of items from the adapter, uses isSame() from SilkComparable to find the items.
     */
    public final void remove(ItemType[] toRemove) {
        for (ItemType item : toRemove) remove(item);
    }

    /**
     * Clears all items from the adapter and notifies the attached ListView.
     */
    public void clear() {
        isChanged = true;
        this.items.clear();
        notifyDataSetChanged();
    }

    /**
     * Gets a list of all items in the adapter.
     */
    public final List<ItemType> getItems() {
        return items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ItemType getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        if (i < 0) i = 0;
        return getItemId(getItem(i));
    }

    protected abstract long getItemId(ItemType item);

    @Override
    public final View getView(int i, View view, ViewGroup parent) {
        if (view == null) {   
            int type = getItemViewType(i);
            view = LayoutInflater.from(context).inflate(getLayout(i, type), null);
        }
        return onViewCreated(i, view, getItem(i), parent);
    }

    /**
     * Resets the changed state of the adapter, indicating that the adapter has not been changed. Every call
     * to a mutator method (e.g. add, set, remove, clear) will set it back to true.
     */
    public void resetChanged() {
        isChanged = false;
    }

    /**
     * Marks the adapter as changed.
     */
    public void markChanged() {
        isChanged = true;
    }

    /**
     * Gets whether or not the adapter has been changed since the last time {#resetChanged} was called.
     */
    public final boolean isChanged() {
        return isChanged;
    }

    /**
     * Gets the scroll state set by a {@link com.afollestad.silk.views.list.SilkListView}.
     */
    public final int getScrollState() {
        return mScrollState;
    }

    /**
     * Used by the {@link com.afollestad.silk.views.list.SilkListView} to update the adapter with its scroll state.
     */
    public final void setScrollState(int state) {
        mScrollState = state;
    }
}