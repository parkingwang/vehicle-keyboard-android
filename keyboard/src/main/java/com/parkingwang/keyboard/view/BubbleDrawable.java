package com.parkingwang.keyboard.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.parkingwang.keyboard.Texts;
import com.parkingwang.vehiclekeyboard.R;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 0.1
 */
public class BubbleDrawable extends Drawable {

    private static final float ANCHOR_Y = 103f / 118;
    private static final float TEXT_CENTER_Y = 41f / 118;

    private final Drawable mBackgroundDrawable;
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    private final float mCNTextSize;
    private final float mENTextSize;

    private String mText;

    public BubbleDrawable(Context context) {
        final Resources resources = context.getResources();
        mBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.pwk_key_bubble_bg);
        setBounds(0, 0, mBackgroundDrawable.getIntrinsicWidth(), mBackgroundDrawable.getIntrinsicHeight());
        mCNTextSize = resources.getDimensionPixelSize(R.dimen.pwk_keyboard_bubble_cn_text_size);
        mENTextSize = resources.getDimensionPixelSize(R.dimen.pwk_keyboard_bubble_en_text_size);
        mTextPaint.setColor(ContextCompat.getColor(context, R.color.pwk_key_pressed_bubble_text));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setFakeBoldText(true);
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    public void setText(String text) {
        mText = text;
        if (Texts.isEnglishLetterOrDigit(text)) {
            mTextPaint.setTextSize(mENTextSize);
        } else {
            mTextPaint.setTextSize(mCNTextSize);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.translate(0, (1 - ANCHOR_Y) * getIntrinsicHeight());
        mBackgroundDrawable.draw(canvas);
        final float textCenterX = getIntrinsicWidth() / 2;
        final float textCenterY = getIntrinsicHeight() * TEXT_CENTER_Y - (mTextPaint.ascent() + mTextPaint.descent()) / 2;
        canvas.drawText(mText, textCenterX, textCenterY, mTextPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        mBackgroundDrawable.setAlpha(alpha);
        mTextPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mTextPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    public int getIntrinsicWidth() {
        return mBackgroundDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBackgroundDrawable.getIntrinsicHeight();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mBackgroundDrawable.setBounds(left, top, right, bottom);
    }
}
