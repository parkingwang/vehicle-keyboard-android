/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.vehiclekeyboard.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.parkingwang.vehiclekeyboard.BuildConfig;
import com.parkingwang.vehiclekeyboard.R;
import com.parkingwang.vehiclekeyboard.core.EngineRunner;
import com.parkingwang.vehiclekeyboard.core.KeyEntry;
import com.parkingwang.vehiclekeyboard.core.KeyType;
import com.parkingwang.vehiclekeyboard.core.KeyboardEntry;
import com.parkingwang.vehiclekeyboard.core.KeyboardType;
import com.parkingwang.vehiclekeyboard.core.NumberType;
import com.parkingwang.vehiclekeyboard.support.Texts;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @since 2017-09-26 0.1
 */
public class KeyboardView extends LinearLayout {

    private static ExecutorService RUNNER_THREAD = Executors.newSingleThreadExecutor();

    private final Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    private int mRowSpace;

    private final EngineRunner mEngine;

    private final List<KeyboardCallback> mKeyboardCallbacks = new CopyOnWriteArrayList<>();

    private KeyboardType mKeyboardType;

    private int mDefaultKeyHeight;

    private boolean mShowBubble = true;

    private float mCNTextSize;
    private float mENTextSize;

    private MotionEvent mLastEvent;

    private final OnClickListener mKeyViewListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!(v instanceof KeyView)) {
                return;
            }
            KeyView keyView = (KeyView) v;
            final KeyEntry key = keyView.getBoundKey();
            Stream.of(mKeyboardCallbacks).forEach(new Consumer<KeyboardCallback>() {
                @Override
                public void accept(KeyboardCallback callback) {
                    if (key.keyType == KeyType.GENERAL) {
                        callback.onTextKey(key.text);
                    } else if (key.keyType == KeyType.DELETE) {
                        callback.onDeleteKey();
                    } else if (key.keyType == KeyType.OK) {
                        callback.onConfirmKey();
                    }
                }
            });
        }
    };

    private final KeyViewCacheHelper mKeyCacheHelper = new KeyViewCacheHelper();

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KeyboardView);
        int type = ta.getInt(R.styleable.KeyboardView_pwkKeyboardType, 0);
        ta.recycle();

        Resources resources = getResources();
        mDefaultKeyHeight = resources.getDimensionPixelSize(R.dimen.pwk_keyboard_key_height);

        mEngine = new EngineRunner(context);
        mKeyboardType = KeyboardType.values()[type];

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
        update("");
    }

    /**
     * 更新车牌键盘
     *
     * @param number 当前已输入的车牌
     */
    public final void update(@NonNull String number) {
        this.update(number, number.length());
    }

    /**
     * 更新车牌键盘
     *
     * @param number 当前已输入的车牌
     * @param index  当前正在修改的车牌坐标
     */
    public final void update(@NonNull String number, int index) {
        this.update(number, index, NumberType.AUTO_DETECT);
    }

    /**
     * 更新车牌键盘。
     * 此操作会触发KeyboardCallback回调。
     *
     * @param number     当前已输入的车牌
     * @param showIndex  当前正在修改的车牌的坐标
     * @param numberType 车牌号类型
     */
    public void update(@NonNull final String number, final int showIndex, final NumberType numberType) {
        final long start = System.nanoTime();
        RUNNER_THREAD.submit(new Runnable() {
            @Override
            public void run() {
                mEngine.start();
                final int finalIndex = showIndex < 0 ? 0 : showIndex;
                final KeyboardEntry keyboard =
                        mEngine.update(mKeyboardType, finalIndex, number, numberType);
                final long escaped = TimeUnit.MILLISECONDS.convert((System.nanoTime() - start), TimeUnit.NANOSECONDS);
                if (BuildConfig.DEBUG) {
                    Log.w("KeyboardView", "--> jskeyboard.engine.update: " + escaped + "ms");
                }
                KeyboardView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        if (keyboard == null) {
                            return;
                        }
                        try {
                            updateKeyboardLayout(keyboard);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        triggerCallback(keyboard);
                    }
                });
            }
        });
    }

    private void triggerCallback(KeyboardEntry keyboard) {
        try {
            for (KeyboardCallback callback : mKeyboardCallbacks) {
                callback.onUpdateKeyboard(keyboard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateKeyboardLayout(KeyboardEntry keyboard) {
        int maxColumn = 0;
        List<List<KeyEntry>> keyRows = keyboard.keyRows;
        for (List<KeyEntry> row : keyRows) {
            if (row.size() > maxColumn) {
                maxColumn = row.size();
            }
        }

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

            mKeyCacheHelper.recyclerKeyViewsInRow(rowLayout, columnSize, mKeyViewListener);
            for (int i = 0, size = keyEntryRow.size(); i < size; i++) {
                KeyEntry key = keyEntryRow.get(i);
                KeyView keyView = (KeyView) rowLayout.getChildAt(i);
                keyView.bindKey(key);
                if (key.keyType == KeyType.DELETE) {
                    keyView.setText("");
                } else {
                    keyView.setText(key.text);
                }
                if(Texts.isLetterOrDigitOnly(key.text)) {
                    keyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mENTextSize);
                } else {
                    keyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCNTextSize);
                }
                keyView.setShowBubble(mShowBubble);
                keyView.setEnabled(key.enabled);
            }
        }
    }

    /**
     * 设置当前键盘类型
     * @param keyboardType 要设置的键盘类型
     */
    public void setKeyboardType(@NonNull KeyboardType keyboardType) {
        mKeyboardType = keyboardType;
    }

    /**
     * 设置键盘状态回调接口
     *
     * @param callback 回调接口
     */
    public void addKeyboardCallback(@NonNull KeyboardCallback callback) {
        mKeyboardCallbacks.add(callback);
    }

    /**
     * 移除键盘状态回调接口
     *
     * @param callback 回调接口
     */
    public void removeKeyboardCallback(@NonNull KeyboardCallback callback) {
        mKeyboardCallbacks.remove(callback);
    }

    /**
     * 设置按键的数字或字母大小
     * @param textSize 按键数字或字母大小，单位为sp
     * @since 0.6
     */
    public void setCNTextSize(float textSize) {
        setCNTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置按键的数字或字母大小
     * @param unit 单位
     * @param textSize 大小的值
     */
    public void setCNTextSize(int unit, float textSize) {
        mCNTextSize = TypedValue.applyDimension(unit, textSize, getResources().getDisplayMetrics());
    }

    /**
     * 设置按键的汉字大小
     * @param textSize 按键汉字大小，单位为sp
     * @since 0.6
     */
    public void setENTextSize(float textSize) {
        setENTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置按键的中文字体大小
     * @param unit 单位
     * @param textSize 大小的值
     */
    public void setENTextSize(int unit, float textSize) {
        mENTextSize = TypedValue.applyDimension(unit, textSize, getResources().getDisplayMetrics());
    }

    public void setShowBubble(boolean showBubble) {
        mShowBubble = showBubble;
    }

    public void start() {
        RUNNER_THREAD.submit(new Runnable() {
            @Override
            public void run() {
                mEngine.start();
            }
        });
    }

    public void stop() {
        RUNNER_THREAD.submit(new Runnable() {
            @Override
            public void run() {
                mEngine.stop();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
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
//        measureChildren(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
//        setMeasuredDimension(width, height);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), 2, mDividerPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() > 1)  {
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
