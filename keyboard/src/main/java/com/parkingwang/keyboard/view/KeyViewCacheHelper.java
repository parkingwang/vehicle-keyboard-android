/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.keyboard.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Stack;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @since 2017-09-27 0.1
 */
final class KeyViewCacheHelper {
    private final Stack<KeyView> mKeyViews = new Stack<>();

    void recyclerKeyRows(KeyboardView keyboardView, int row) {
        int childCount = keyboardView.getChildCount();
        if (childCount > row) {
            for (int i = 0, trimCount = childCount - row; i < trimCount; i++) {
                trimKeyRowLayout(keyboardView, 0);
            }
        } else if (childCount < row) {
            for (int i = childCount; i < row; i++) {
                fixKeyRowLayout(keyboardView);
            }
        }
    }

    private void trimKeyRowLayout(KeyboardView keyboardView, int row) {
        KeyRowLayout keyRow = (KeyRowLayout) keyboardView.getChildAt(row);
        int column = keyRow.getChildCount();
        final int targetIndex = 0;
        for (int j = 0; j < column; j++) {
            // 移除第index位之后，原来index+1位会变成index位
            pushKeyView((KeyView) keyRow.getChildAt(targetIndex));
            keyRow.removeViewAt(targetIndex);
        }
        keyboardView.removeView(keyRow);
    }

    private void fixKeyRowLayout(KeyboardView keyboardView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        KeyRowLayout keyRowLayout = new KeyRowLayout(keyboardView.getContext());
        keyRowLayout.setLayoutParams(params);
        keyboardView.addView(keyRowLayout, 0);
    }

    void recyclerKeyViewsInRow(KeyRowLayout keyRow, int targetCount, View.OnClickListener listener) {
        int childCount = keyRow.getChildCount();
        if (childCount < targetCount) {
            for (int i = childCount; i < targetCount; i++) {
                keyRow.addView(pullKeyView(keyRow.getContext(), listener));
            }
        } else if (childCount > targetCount) {
            trimKeyViews(keyRow, targetCount, childCount);
        }
    }

    private void trimKeyViews(KeyRowLayout keyRow, int targetCount, int childCount) {
        final int targetIndex = targetCount;
        for (int i = targetCount; i < childCount; i++) {
            // 移除第index位之后，原来index+1位会变成index位
            KeyView keyView = (KeyView) keyRow.getChildAt(targetIndex);
            keyRow.removeViewAt(targetIndex);
            pushKeyView(keyView);
        }
    }

    private KeyView pullKeyView(Context context, View.OnClickListener listener) {
        if (mKeyViews.empty()) {
            KeyView keyView = new KeyView(context);
            keyView.setOnClickListener(listener);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            keyView.setLayoutParams(params);
            return keyView;
        } else {
            return mKeyViews.pop();
        }
    }

    private void pushKeyView(KeyView keyView) {
        mKeyViews.push(keyView);
    }
}
