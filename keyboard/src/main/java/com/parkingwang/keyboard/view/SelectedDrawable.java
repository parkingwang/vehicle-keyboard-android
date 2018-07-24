package com.parkingwang.keyboard.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @since 2018-07-24
 */
public class SelectedDrawable extends Drawable {

    protected float mRadius;
    protected Rect mRect = new Rect();
    protected Position mPosition = Position.FIRST;
    protected final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    protected final Path mPath = new Path();
    protected final RectF mPathRectF = new RectF();

    public SelectedDrawable() {
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public void setWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    public Rect getRect() {
        return mRect;
    }

    public void setPosition(@NonNull Position position) {
        mPosition = position;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float strokeWidthOffset = mPaint.getStrokeWidth() / 2;
        int left = mRect.left;
        int top = mRect.top + (int) strokeWidthOffset;
        int right = mRect.right;
        int bottom = mRect.bottom - (int) strokeWidthOffset;
        final float[] radiusArray = new float[8];
        if (mPosition == Position.FIRST) {
            left += strokeWidthOffset;
            radiusArray[0] = mRadius;
            radiusArray[1] = mRadius;
            radiusArray[6] = mRadius;
            radiusArray[7] = mRadius;
        } else if (mPosition == Position.LAST) {
            right -= strokeWidthOffset;
            radiusArray[2] = mRadius;
            radiusArray[3] = mRadius;
            radiusArray[4] = mRadius;
            radiusArray[5] = mRadius;
        }
        mPath.reset();
        mPathRectF.set(left, top, right, bottom);
        mPath.addRoundRect(mPathRectF, radiusArray, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    enum Position {
        FIRST,
        MIDDLE,
        LAST
    }
}
