/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.keyboard.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.parkingwang.keyboard.Texts;
import com.parkingwang.keyboard.engine.KeyEntry;
import com.parkingwang.keyboard.engine.KeyType;
import com.parkingwang.keyboard.engine.KeyboardEngine;
import com.parkingwang.keyboard.engine.KeyboardEntry;
import com.parkingwang.keyboard.engine.NumberType;
import com.parkingwang.vehiclekeyboard.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import hugo.weaving.DebugLog;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @author 陈永佳 (chenyongjia@parkingwang.com)
 */
public class KeyboardView extends LinearLayout {
    private static final String TAG = "KeyboardView";

    private final Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    private int mRowSpace;
    private int mDefaultKeyHeight;
    private boolean mShowBubble = true;
    private float mCNTextSize;
    private float mENTextSize;
    private MotionEvent mLastEvent;

    private final List<OnKeyboardChangedListener> mKeyboardChangedListeners = new CopyOnWriteArrayList<>();
    private final KeyboardEngine mKeyboardEngine = new KeyboardEngine();
    private final KeyViewCacheHelper mKeyCacheHelper = new KeyViewCacheHelper();

    private final OnClickListener mOnKeyPressedListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!(v instanceof KeyView)) {
                return;
            }
            final KeyEntry key = ((KeyView) v).getBoundKey();
            onKeyPressed(key);
        }
    };

    // 缓存当前状态，用于切换“更多”与“返回”的状态
    private String mStashedNumber;
    private int mStashedIndex;
    private boolean mStashedShowMore;
    private NumberType mStashedNumberType;

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final Resources resources = getResources();
        mDefaultKeyHeight = resources.getDimensionPixelSize(R.dimen.pwk_keyboard_key_height);

        setOrientation(LinearLayout.VERTICAL);
        final Drawable rowDivider = ContextCompat.getDrawable(context, R.drawable.pwk_space_vertical);
        mRowSpace = rowDivider.getIntrinsicHeight();
        setDividerDrawable(rowDivider);
        setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        setBackgroundResource(R.color.pwk_keyboard_background);

        final int dividerColor = ContextCompat.getColor(context, R.color.pwk_keyboard_divider);
        mDividerPaint.setColor(dividerColor);

        final int leftPadding = getResources().getDimensionPixelSize(R.dimen.pwk_keyboard_padding_left);
        final int topPadding = getResources().getDimensionPixelSize(R.dimen.pwk_keyboard_padding_top);
        final int rightPadding = getResources().getDimensionPixelSize(R.dimen.pwk_keyboard_padding_right);
        final int bottomPadding = getResources().getDimensionPixelSize(R.dimen.pwk_keyboard_padding_bottom);
        setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        setClipChildren(false);
        setClipToPadding(false);

        mCNTextSize = getResources().getDimensionPixelSize(R.dimen.pwk_keyboard_key_cn_text_size);
        mENTextSize = getResources().getDimensionPixelSize(R.dimen.pwk_keyboard_key_en_text_size);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 避免键盘面板的点击事件往下层传
            }
        });
    }

    public KeyboardEngine getKeyboardEngine() {
        return mKeyboardEngine;
    }

    /**
     * 更新车牌键盘。
     * 此操作会触发KeyboardCallback回调。
     *
     * @param number          当前已输入的车牌
     * @param showIndex       当前正在修改的车牌的坐标
     * @param fixedNumberType 车牌号类型
     */
    @DebugLog
    public void update(@NonNull final String number, final int showIndex, final boolean showMore, final NumberType fixedNumberType) {
        mStashedNumber = number;
        mStashedIndex = showIndex;
        mStashedShowMore = showMore;
        mStashedNumberType = fixedNumberType;
        // 更新键盘布局
        final KeyboardEntry keyboard = mKeyboardEngine.update(number, showIndex, showMore, fixedNumberType);
        renderLayout(keyboard);
        // 触发键盘变更回调
        try {
            for (OnKeyboardChangedListener listener : mKeyboardChangedListeners) {
                listener.onKeyboardChanged(keyboard);
            }
        } catch (Exception e) {
            Log.e(TAG, "On keyboard changed", e);
        }
    }

    ////

    private void onKeyPressed(KeyEntry key) {
        switch (key.keyType) {
            case FUNC_OK:
                for (OnKeyboardChangedListener l : mKeyboardChangedListeners) {
                    l.onConfirmKey();
                }
                break;

            case FUNC_DELETE:
                for (OnKeyboardChangedListener l : mKeyboardChangedListeners) {
                    l.onDeleteKey();
                }
                break;

            default:
            case GENERAL:
                for (OnKeyboardChangedListener l : mKeyboardChangedListeners) {
                    l.onTextKey(key.text);
                }
                break;

            case FUNC_MORE:
                update(mStashedNumber, mStashedIndex, true, mStashedNumberType);
                break;

            case FUNC_BACK:
                update(mStashedNumber, mStashedIndex, false, mStashedNumberType);
                break;


        }
    }

    /**
     * 设置键盘状态回调接口
     *
     * @param callback 回调接口
     */
    public void addKeyboardChangedListener(@NonNull OnKeyboardChangedListener callback) {
        mKeyboardChangedListeners.add(callback);
    }

    /**
     * 移除键盘状态回调接口
     *
     * @param callback 回调接口
     */
    public void removeKeyboardChangedListener(@NonNull OnKeyboardChangedListener callback) {
        mKeyboardChangedListeners.remove(callback);
    }

    /**
     * 设置是否显示浮动提示View
     *
     * @param showBubble 是否显示
     */
    public void setShowBubble(boolean showBubble) {
        mShowBubble = showBubble;
    }

    /**
     * 设置按键的数字或字母大小
     *
     * @param textSize 按键数字或字母大小，单位为sp
     * @since 0.6
     */
    public void setCNTextSize(float textSize) {
        setCNTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置按键的数字或字母大小
     *
     * @param unit     单位
     * @param textSize 大小的值
     */
    public void setCNTextSize(int unit, float textSize) {
        mCNTextSize = TypedValue.applyDimension(unit, textSize, getResources().getDisplayMetrics());
    }

    /**
     * 设置按键的汉字大小
     *
     * @param textSize 按键汉字大小，单位为sp
     * @since 0.6
     */
    public void setENTextSize(float textSize) {
        setENTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置按键的中文字体大小
     *
     * @param unit     单位
     * @param textSize 大小的值
     */
    public void setENTextSize(int unit, float textSize) {
        mENTextSize = TypedValue.applyDimension(unit, textSize, getResources().getDisplayMetrics());
    }

    @DebugLog
    private void renderLayout(KeyboardEntry keyboard) {
        final List<List<KeyEntry>> keyRows = keyboard.keyRows;
        // 以第一行的键盘数量为基准
        final int maxColumn = keyRows.get(0).size();

        final int rowSize = keyRows.size();
        mKeyCacheHelper.recyclerKeyRows(this, rowSize);

        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
            List<KeyEntry> keyEntryRow = keyRows.get(rowIndex);
            KeyRowLayout rowLayout = (KeyRowLayout) getChildAt(rowIndex);
            rowLayout.setMaxColumn(maxColumn);

            final int columnSize = keyEntryRow.size();
            int funKeyCount = 0;
            for (KeyEntry keyEntry : keyEntryRow) {
                if (keyEntry.isFunKey) {
                    funKeyCount++;
                }
            }
            rowLayout.setFunKeyCount(funKeyCount);

            mKeyCacheHelper.recyclerKeyViewsInRow(rowLayout, columnSize, mOnKeyPressedListener);
            for (int i = 0, size = keyEntryRow.size(); i < size; i++) {
                KeyEntry key = keyEntryRow.get(i);
                KeyView keyView = (KeyView) rowLayout.getChildAt(i);
                keyView.bindKey(key);
                if (key.keyType == KeyType.FUNC_DELETE) {
                    keyView.setText("");
                } else {
                    keyView.setText(key.text);
                }
                if (Texts.isEnglishLetterOrDigit(key.text)) {
                    keyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mENTextSize);
                } else {
                    keyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCNTextSize);
                }
                keyView.setShowBubble(mShowBubble);
                keyView.setEnabled(key.enabled);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);

        final int rows = getChildCount();
        if (heightMode == MeasureSpec.AT_MOST) {
            height = rows * mDefaultKeyHeight + (rows - 1) * mRowSpace + getPaddingTop() + getPaddingBottom();
            for (int i = 0; i < rows; i++) {
                getChildAt(i).getLayoutParams().height = mDefaultKeyHeight;
            }
        } else if (heightMode == MeasureSpec.EXACTLY) {
            if (rows > 0) {
                int childHeight = (height - getPaddingTop() - getPaddingBottom() - (rows - 1) * mRowSpace) / rows;
                for (int i = 0; i < rows; i++) {
                    getChildAt(i).getLayoutParams().height = childHeight;
                }
            }
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), 2, mDividerPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() > 1) {
            if (ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                float x = ev.getX(ev.getActionIndex());
                float y = ev.getY(ev.getActionIndex());
                if (mLastEvent != null &&
                        x >= 0 && x < getWidth() && y >= 0 && y <= getHeight()) {
                    MotionEvent cancelEvent = MotionEvent.obtain(mLastEvent.getDownTime(),
                            mLastEvent.getEventTime(), MotionEvent.ACTION_UP,
                            mLastEvent.getX(), mLastEvent.getY(), mLastEvent.getMetaState());
                    super.dispatchTouchEvent(cancelEvent);
                    if (cancelEvent != null) {
                        cancelEvent.recycle();
                    }
                    return true;
                }
            }
        }
        mLastEvent = ev;
        return super.dispatchTouchEvent(ev);
    }
}
