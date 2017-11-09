/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.vehiclekeyboard.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.parkingwang.vehiclekeyboard.R;
import com.parkingwang.vehiclekeyboard.core.KeyEntry;
import com.parkingwang.vehiclekeyboard.core.KeyType;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @since 2017-09-26 0.1
 */
class KeyRowLayout extends LinearLayout {
    /**
     * 单行最大键数达到这个数，则显示窄间隔
     */
    private static final int NARROW_SPACE_KEY_COUNT = 11;

    /**
     * 删除键与确定的宽度计算方法如下：
     * 删除与确定的宽度毕竟为：3:5。删除与确定及间距所占宽度与单行10个键盘的情况下3个按键及其间间距的总和相等。
     */
    private static final float RATIO_FUN_DEL = 3.0f / 8;
    private static final float RATIO_FUN_CONFIRM = 5.0f / 8;

    private int mGeneralKeySpace;
    private int mFunKeySpace;

    private int mMaxColumn;
    private int mFunKeyIndex;
    private int mWidthUnused;

    private int mFunKeyCount;

    public KeyRowLayout(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        final Drawable keyDivider = ContextCompat.getDrawable(context, R.drawable.pwk_space_horizontal);
        mFunKeySpace = keyDivider.getIntrinsicWidth();
        setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        setClipToPadding(false);
        setClipChildren(false);
    }

    public void setMaxColumn(int maxColumn) {
        mMaxColumn = maxColumn;
        final Drawable keyDivider;
        if (mMaxColumn < NARROW_SPACE_KEY_COUNT) {
            keyDivider = ContextCompat.getDrawable(getContext(), R.drawable.pwk_space_horizontal);
        } else {
            keyDivider = ContextCompat.getDrawable(getContext(), R.drawable.pwk_space_horizontal_narrow);
        }
        mGeneralKeySpace = keyDivider.getIntrinsicWidth();
        setDividerDrawable(keyDivider);
    }

    public void setFunKeyCount(int funKeyCount) {
        mFunKeyCount = funKeyCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();

        // 删除+确定（不含间隔）：10个键的情况下，3个键的宽度+1个间隔
        int delAndConfirmWidth = (width - 9 * mFunKeySpace) * 3 / 10 + mFunKeySpace;
        int deleteKeyWidth = (int) (delAndConfirmWidth * RATIO_FUN_DEL);
        int confirmKeyWidth = (int) (delAndConfirmWidth * RATIO_FUN_CONFIRM);

        float generalKeyWidth = getGeneralKeyWidth(width, delAndConfirmWidth, confirmKeyWidth);

        int widthUsed = 0;
        mFunKeyIndex = 0;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (!(view instanceof KeyView)) {
                continue;
            }
            KeyView keyView = (KeyView) view;
            KeyEntry keyEntry = keyView.getBoundKey();
            LayoutParams params = (LayoutParams) keyView.getLayoutParams();
            if (keyEntry.keyType == KeyType.GENERAL) {
                params.width = (int) generalKeyWidth;
            } else {
                if (keyEntry.keyType == KeyType.DELETE) {
                    if (mFunKeyCount == 2) {
                        // 确定和删除都存在的情况下按设计的删除按键的大小
                        params.width = deleteKeyWidth;
                        // 2个功能键的情况下，当一行最多有11个键时，功能键的间隔和普通键的间隔不同，需要加上这个差值
                        widthUsed += (mFunKeySpace - mGeneralKeySpace);
                    } else {
                        // 否则按确定的大小
                        params.width = confirmKeyWidth;
                    }
                } else {
                    params.width = confirmKeyWidth;
                }
                if (mFunKeyIndex == 0) {
                    mFunKeyIndex = i;
                }
            }
            widthUsed += (params.width + mGeneralKeySpace);
        }
        widthUsed -= mGeneralKeySpace;
        mWidthUnused = width - widthUsed;
        if (mFunKeyCount > 0) {
            setPadding(0, 0, 0, 0);
        } else {
            int padding = mWidthUnused / 2;
            setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float getGeneralKeyWidth(int width, int delAndConfirmWidth, int confirmKeyWidth) {
        float generalKeyWidth = (width - (mMaxColumn - 1) * mGeneralKeySpace) / mMaxColumn;
        int funKeyWidthUsed = 0;
        if (mFunKeyCount == 1) {
            funKeyWidthUsed = confirmKeyWidth + mGeneralKeySpace;
        } else if (mFunKeyCount == 2) {
            funKeyWidthUsed = delAndConfirmWidth + mGeneralKeySpace;
        }
        int generalKeyCount = getChildCount() - mFunKeyCount;
        float availableGeneralKeyWidth = width - funKeyWidthUsed - (generalKeyCount - 1) * mGeneralKeySpace;
        if (availableGeneralKeyWidth < generalKeyWidth * generalKeyCount) {
            generalKeyWidth = availableGeneralKeyWidth / generalKeyCount;
        }
        return generalKeyWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mFunKeyCount > 0) {
            // 位移删除键
            ViewCompat.offsetLeftAndRight(getChildAt(mFunKeyIndex), mWidthUnused);
            if (mFunKeyCount == 2) {
                // 位移确定键
                ViewCompat.offsetLeftAndRight(getChildAt(mFunKeyIndex + 1), mWidthUnused + (mFunKeySpace - mGeneralKeySpace));
            }
        }
    }
}
