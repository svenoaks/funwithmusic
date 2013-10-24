package com.afollestad.silk.images;

import android.content.Context;
import android.view.View;

/**
 * Holds width and height values.
 */
public class Dimension {

    private final int width;
    private final int height;

    /**
     * Initializes the Dimension with equal width in height.
     *
     * @param squarePx The value to set for both the width and the height.
     */
    public Dimension(int squarePx) {
        width = squarePx;
        height = squarePx;
    }

    /**
     * Initializes the Dimension with different width and height.
     */
    public Dimension(int widthPx, int heightPx) {
        width = widthPx;
        height = heightPx;
    }

    /**
     * Initializes the Dimension with equal width and height, converts the specified dp value to pixels.
     *
     * @param context  The context that is required for dp conversion.
     * @param squareDp The value in dp to set for both the width and height.
     */
    public Dimension(Context context, float squareDp) {
        int px = dpToPx(context, squareDp);
        width = px;
        height = px;
    }

    /**
     * Initializes the Dimension with width and height equal to the measured dimensions of a view.\
     */
    public Dimension(View view) {
        width = view.getMeasuredWidth();
        height = view.getMeasuredHeight();
    }

    /**
     * Initializes the Dimension with different width and height, converts the specified dp values to pixels.
     *
     * @param context  The context that is required for dp conversion.
     * @param widthDp  The value in dp to set for the width.
     * @param heightDp The value in dp to set for the height.
     */
    public Dimension(Context context, float widthDp, float heightDp) {
        width = dpToPx(context, widthDp);
        height = dpToPx(context, heightDp);
    }

    private static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Gets the width of the Dimension in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the Dimension in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns true if both the width and height are zero.
     */
    public boolean isZero() {
        return width == 0 && height == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Dimension))
            return false;
        Dimension other = (Dimension) o;
        return other.getWidth() == this.getWidth() && other.getHeight() == this.getHeight();
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}