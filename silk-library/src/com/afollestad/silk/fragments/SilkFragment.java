package com.afollestad.silk.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * The base of all other fragments in the library; contains methods that help maintain consistency among all fragments,
 * also provides various convenience methods.
 *
 * @author Aidan Follestad (afollestad)
 */
public abstract class SilkFragment extends Fragment {

    private boolean mAttached;

    /**
     * Gets the fragment layout to be inflated.
     */
    protected abstract int getLayout();

    /**
     * Gets the title set to the activity when the Fragment is attached.
     */
    public abstract String getTitle();

    /**
     * Called when the Fragment becomes visible or invisible to the user.
     * <p/>
     * This works even when in a ViewPager when Fragments that are not actually visible are initialized off to the side.
     * <p/>
     * Sometimes {#getUserVisibleHint}, {#isVisible}, {#setUserVisibleHint}, etc. can be deceiving. This is the answer.
     */
    protected void onVisibilityChanged(boolean visible) {
        // Nothing is done by default
    }

    private void notifyVisibility(boolean visible) {
        if (visible) {
            if (getActivity() != null && getTitle() != null)
                getActivity().setTitle(getTitle());
            if (mAttached) {
                // Don't allow multiple notifications
                return;
            }
        }
        mAttached = visible;
        onVisibilityChanged(visible);
    }

    /**
     * Gets whether or not the fragment is attached and visible to the user.
     */
    public final boolean isActuallyVisible() {
        return mAttached;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint())
            notifyVisibility(true);
        else if (mAttached)
            notifyVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAttached)
            notifyVisibility(false);
    }

    @Override
    public final void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) notifyVisibility(true);
        else if (isVisible()) notifyVisibility(false);
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public final void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // This is overridden so sub-classes can't override it, use onVisibilityChange() instead.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), null);
    }

    public final void runOnUiThread(Runnable runnable) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(runnable);
    }
}
