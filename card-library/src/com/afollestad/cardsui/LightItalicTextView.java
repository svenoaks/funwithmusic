package com.afollestad.cardsui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Aidan Follestad (afollestad)
 */
public class LightItalicTextView extends TextView {

    public LightItalicTextView(Context context) {
        super(context);
        init(context);
    }

    public LightItalicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LightItalicTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "Roboto-LightItalic.ttf");
            setTypeface(tf);
        } catch (RuntimeException e) {
            throw new RuntimeException("Make sure you copied the 'assets' folder from Silk to your own project; " + e.getMessage());
        }
    }
}
