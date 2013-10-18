package com.afollestad.silk.caching;

/**
 * @author Aidan Follestad (afollestad)
 */
public enum LimiterBehavior {

    /**
     * When the cache limit is reached, the cache is cleared.
     */
    CLEAR(0),
    /**
     * When the cache limit is reached, items are trimmed from the top (index 0).
     */
    REMOVE_TOP(1),
    /**
     * When the cache limit is reached, items are trimmed from the bottom (index size() - 1).
     */
    REMOVE_BOTTOM(2);
    private final int mValue;

    LimiterBehavior(int value) {
        mValue = value;
    }

    public static LimiterBehavior valueOf(int value) {
        switch (value) {
            default:
                return CLEAR;
            case 1:
                return REMOVE_TOP;
            case 2:
                return REMOVE_BOTTOM;
        }
    }

    public int intValue() {
        return mValue;
    }
}
