package com.afollestad.silk.views.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class SilkAspectImageView extends SilkImageView {

    public SilkAspectImageView(Context context) {
        super(context);
        super.invalidateOnLoad = true;
    }

    public SilkAspectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.invalidateOnLoad = true;
    }

    public SilkAspectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.invalidateOnLoad = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * This method insures the image view keeps it's aspect ratio when the view is stretched
         * (like in a RelativeLayout where fill_parent or wrap_content are used).
         */
        Drawable d = getDrawable();
        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
