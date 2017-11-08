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

    /** 删除键与确定的宽度计算方法如下：
     * 删除与确定的宽度毕竟为：3:5。删除与确定及间距所占宽度与单行10个键盘的情况下3个按键及其间间距的总和相等。
     */
    private static final float RATIO_FUN_DEL = 3.0f / 8;
    private static final float RATIO_FUN_CONFIRM = 5.0f / 8;

    private int mKeySpace;

    private int mMaxColumn;
    private int mFunKeyIndex;
    private int mWidthUnused;

    private int mFunKeyCount;

    public KeyRowLayout(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        final Drawable keyDivider = ContextCompat.getDrawable(context, R.drawable.pwk_space_horizontal);
        mKeySpace = keyDivider.getIntrinsicWidth();
        setDividerDrawable(keyDivider);
        setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        setClipToPadding(false);
        setClipChildren(false);
    }

    public void setMaxColumn(int maxColumn) {
        mMaxColumn = maxColumn;
    }

    public void setFunKeyCount(int funKeyCount) {
        mFunKeyCount = funKeyCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();

        // 删除+确定（不含间隔）：3个键的宽度+1个间隔
        int delAndConfirmWidth = (width - 9 * mKeySpace) * 3 / 10 + mKeySpace;
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
            widthUsed += (params.width + mKeySpace);

//            keyView.measure(MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY),
//                    heightMeasureSpec);
        }
        widthUsed -= mKeySpace;
        mWidthUnused = width - widthUsed;
        if (mFunKeyCount > 0) {
            setPadding(0, 0, 0, 0);
        } else {
            int padding = mWidthUnused / 2;
            setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
        }

//        setMeasuredDimension(width, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float getGeneralKeyWidth(int width, int delAndConfirmWidth, int confirmKeyWidth) {
        float generalKeyWidth = (width - (mMaxColumn - 1) * mKeySpace) / mMaxColumn;
        int funKeyWidthUsed = 0;
        if (mFunKeyCount == 1) {
            funKeyWidthUsed = confirmKeyWidth + mKeySpace;
        } else if (mFunKeyCount == 2) {
            funKeyWidthUsed = delAndConfirmWidth + mKeySpace;
        }
        int generalKeyCount = getChildCount() - mFunKeyCount;
        float availableGeneralKeyWidth = width - funKeyWidthUsed - (generalKeyCount - 1) * mKeySpace;
        if (availableGeneralKeyWidth < generalKeyWidth * generalKeyCount) {
            generalKeyWidth = availableGeneralKeyWidth / generalKeyCount;
        }
        return generalKeyWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mFunKeyCount > 0) {
            for (int i = mFunKeyIndex, end = getChildCount(); i < end; i++) {
                ViewCompat.offsetLeftAndRight(getChildAt(i), mWidthUnused);
            }
        }
    }
}
