package com.afollestad.silk.views.image;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;

public class SilkRoundedImageView extends SilkImageView {

    public SilkRoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paintBorder = new Paint();
        paintBorder.setColor(Color.DKGRAY);
        paintBorder.setAntiAlias(true);
    }

    private final Paint paint;
    private final Paint paintBorder;
    private int borderWidth = 2;

    public SilkRoundedImageView setBorderWidth(int width) {
        borderWidth = width;
        return this;
    }

    @Override
    protected Bitmap onPostProcess(Bitmap image) {
        Bitmap circleBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circleBitmap);
        BitmapShader shader = new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        int circleCenter = image.getWidth() / 2;
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter + borderWidth, paintBorder);
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter, paint);
        return circleBitmap;
    }
}