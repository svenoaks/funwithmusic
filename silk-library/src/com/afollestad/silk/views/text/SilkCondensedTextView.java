package com.afollestad.silk.views.text;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * A TextView that automatically sets its typeface to Roboto Condensed. The font is loaded
 * from the library's assets so it will work on any version of Android.
 *
 * @author Aidan Follestad
 */
public class SilkCondensedTextView extends TextView {

    public SilkCondensedTextView(Context context) {
        super(context);
        init(context);
    }

    public SilkCondensedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SilkCondensedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "Roboto-Condensed.ttf");
            setTypeface(tf);
        } catch (RuntimeException e) {
            throw new RuntimeException("Make sure you copied the 'assets' folder from Silk to your own project; " + e.getMessage());
        }
    }
}
