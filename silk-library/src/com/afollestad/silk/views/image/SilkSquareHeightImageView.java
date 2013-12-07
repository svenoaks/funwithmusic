package com.afollestad.silk.views.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class SilkSquareHeightImageView extends SilkImageView {

    public SilkSquareHeightImageView(Context context) {
        super(context);
        super.invalidateOnLoad = true;
    }

    public SilkSquareHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.invalidateOnLoad = true;
    }

    public SilkSquareHeightImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.invalidateOnLoad = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (height > 0) {
                setMeasuredDimension(height, height);
            } else {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
