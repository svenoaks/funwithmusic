package com.afollestad.silk.caching;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
class SilkCacheBaseLimiter<Item> {

    public SilkCacheBaseLimiter(Context context, String name) {
        mContext = context;
        mName = name;
    }

    private final Context mContext;
    private final String mName;

    protected Context getContext() {
        return mContext;
    }

    protected String getName() {
        return mName;
    }

    protected void log(String message) {
        Log.d("SilkCache-" + getName(), message);
    }

    protected SharedPreferences getLimiterPrefs() {
        return mContext.getSharedPreferences("[silk-cache-limiters]", Context.MODE_PRIVATE);
    }

    private SilkCacheLimiter getLimiter() {
        if (!hasLimiter()) return null;
        return new SilkCacheLimiter(getLimiterPrefs().getString(mName, null));
    }

    public final boolean hasLimiter() {
        return getLimiterPrefs().contains(mName);
    }

    protected boolean atLimit(List<Item> buffer) {
        SilkCacheLimiter mLimiter = getLimiter();
        return mLimiter != null && buffer.size() > mLimiter.getSizeLimit();
    }

    protected List<Item> performLimit(List<Item> buffer) {
        SilkCacheLimiter mLimiter = getLimiter();
        log("Performing limiting on cache...");
        if (mLimiter.getBehavior() == LimiterBehavior.CLEAR) {
            buffer.clear();
            return buffer;
        }
        int removed = 0;
        log("Limiting behavior: " + mLimiter.getBehavior().name());
        while (buffer.size() > mLimiter.getSizeLimit()) {
            removed++;
            switch (mLimiter.getBehavior()) {
                case REMOVE_TOP:
                    buffer.remove(0);
                    break;
                case REMOVE_BOTTOM:
                    buffer.remove(buffer.size() - 1);
                    break;
            }
        }
        log("Removed " + removed + " items for limiting");
        return buffer;
    }
}
