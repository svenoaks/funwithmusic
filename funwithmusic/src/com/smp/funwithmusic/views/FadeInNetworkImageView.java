package com.smp.funwithmusic.views;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.*;
import android.util.AttributeSet;
 
import com.android.volley.toolbox.NetworkImageView;
 
public class FadeInNetworkImageView extends NetworkImageView {
 
    private static final int FADE_IN_TIME_MS = 150;
 
    public FadeInNetworkImageView(Context context) {
        super(context);
    }
 
    public FadeInNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public FadeInNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    public void setImageBitmap(Bitmap bm) {
    	setAlpha(0f);
        super.setImageBitmap(bm);
        animate().alpha(1f).setDuration(FADE_IN_TIME_MS);
        /*
    	TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(android.R.color.transparent),
                new BitmapDrawable(getContext().getResources(), bm)
        });
 		
        setImageDrawable(td);
        td.startTransition(FADE_IN_TIME_MS);
        */
    }
}
