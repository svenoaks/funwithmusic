package com.afollestad.silk.caching;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SilkCacheLimiter {

    /**
     * Initializes a new SilkCacheLimiter instance.
     *
     * @param sizeLimit The size limit of the cache (triggers the specified behavior).
     * @param behavior  The behavior of the cache when the size limit is reached.
     */
    public SilkCacheLimiter(int sizeLimit, LimiterBehavior behavior) {
        if (sizeLimit < 1) throw new IllegalArgumentException("The size limit cannot be less than 1.");
        mSizeLimit = sizeLimit;
        mBehavior = behavior;
    }

    public SilkCacheLimiter(String preference) {
        String[] split = preference.split(":");
        mSizeLimit = Integer.parseInt(split[0]);
        mBehavior = LimiterBehavior.valueOf(Integer.parseInt(split[1]));
    }

    private final int mSizeLimit;
    private final LimiterBehavior mBehavior;

    public int getSizeLimit() {
        return mSizeLimit;
    }

    public LimiterBehavior getBehavior() {
        return mBehavior;
    }

    @Override
    public String toString() {
        return mSizeLimit + ":" + mBehavior.intValue();
    }
}
