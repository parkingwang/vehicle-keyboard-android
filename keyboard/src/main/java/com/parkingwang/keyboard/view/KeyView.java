/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.keyboard.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

import com.parkingwang.keyboard.engine.KeyEntry;
import com.parkingwang.keyboard.engine.KeyType;
import com.parkingwang.vehiclekeyboard.R;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @since 2017-09-26 0.1
 */
final class KeyView extends TextView {
    private final BubbleDrawable mBubbleDrawable;
    private KeyEntry mBoundKey;
    private Drawable mDeleteDrawable;
    private boolean mDrawPressedText = false;

    private boolean mShowBubble;
    private int mBubbleTextColor = -1;
    private ColorStateList mOkKeyBackgroundColor;

    public KeyView(Context context) {
        this(context, null);
    }

    public KeyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPadding(0, 0, 0, 0);
        setGravity(Gravity.CENTER);
        mBubbleDrawable = new BubbleDrawable(context);
    }

    public void setBubbleTextColor(int bubbleTextColor) {
        mBubbleTextColor = bubbleTextColor;
        if (mBubbleTextColor != -1) {
            mBubbleDrawable.setTextColor(mBubbleTextColor);
        }
    }
    public void setOkKeyBackgroundColor(ColorStateList okKeyBackgroundColor) {
        mOkKeyBackgroundColor = okKeyBackgroundColor;
    }


    public KeyEntry getBoundKey() {
        return mBoundKey;
    }

    public void bindKey(KeyEntry bindKey) {
        mBoundKey = bindKey;
        mDrawPressedText = false;
        if (bindKey.keyType == KeyType.FUNC_OK) {
            final Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.pwk_keyboard_key_general_bg);
            if (mOkKeyBackgroundColor == null) {
                mOkKeyBackgroundColor = ContextCompat.getColorStateList(getContext(), R.color.pwk_keyboard_key_ok_tint_color);
            }
            final Drawable tintDrawable = DrawableTint.tint(drawable, mOkKeyBackgroundColor);
            setBackgroundDrawable(tintDrawable);
            setTextColor(ContextCompat.getColorStateList(getContext(), R.color.pwk_keyboard_key_ok_text));
        } else {
            setTextColor(ContextCompat.getColorStateList(getContext(), R.color.pwk_keyboard_key_text));
            setBackgroundResource(R.drawable.pwk_keyboard_key_general_bg);
        }
    }

    public void setShowBubble(boolean showBubble) {
        mShowBubble = showBubble;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (mBubbleDrawable != null) {
            mBubbleDrawable.setText(String.valueOf(text));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        KeyEntry keyEntry = mBoundKey;
        if (keyEntry == null) {
            return;
        }

        if (keyEntry.keyType == KeyType.FUNC_DELETE) {
            drawDeleteKey(canvas);
        } else if (keyEntry.keyType == KeyType.GENERAL && mDrawPressedText) {
            canvas.save();
            canvas.translate((getWidth() - mBubbleDrawable.getIntrinsicWidth()) / 2,
                    -mBubbleDrawable.getIntrinsicHeight());
            mBubbleDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private void drawDeleteKey(Canvas canvas) {
        if (mDeleteDrawable == null) {
            mDeleteDrawable = ContextCompat.getDrawable(getContext(), R.drawable.pwk_key_delete);
            mDeleteDrawable.setBounds(0, 0, mDeleteDrawable.getIntrinsicWidth(), mDeleteDrawable.getIntrinsicHeight());
        }
        canvas.save();
        canvas.translate((getWidth() - mDeleteDrawable.getIntrinsicWidth()) / 2,
                (getHeight() - mDeleteDrawable.getIntrinsicHeight()) / 2);
        mDeleteDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mShowBubble || !isEnabled()) {
            return super.onTouchEvent(event);
        }
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mDrawPressedText = true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            float x = event.getX(event.getActionIndex());
            float y = event.getY(event.getActionIndex());
            if (mDrawPressedText && (x < 0 || x > getWidth() || y < 0 || y > getHeight())) {
                mDrawPressedText = false;
                invalidate();
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mDrawPressedText = false;
            invalidate();
        }
        if (mDrawPressedText) {
            invalidate();
        }
        return super.onTouchEvent(event);
    }
}
