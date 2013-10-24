package com.afollestad.silk.views.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class SilkSquareImageView extends SilkImageView {

    public SilkSquareImageView(Context context) {
        super(context);
        super.invalidateOnLoad = true;
    }

    public SilkSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.invalidateOnLoad = true;
    }

    public SilkSquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.invalidateOnLoad = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (width > 0)
                setMeasuredDimension(width, width);
            else
                setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
