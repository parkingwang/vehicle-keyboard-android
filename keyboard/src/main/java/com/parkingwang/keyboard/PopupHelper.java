package com.parkingwang.keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.parkingwang.keyboard.view.KeyboardView;
import com.parkingwang.vehiclekeyboard.R;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 0.4.1
 * @since 2017-11-03 0.1
 */
public class PopupHelper {

    public static boolean showToActivity(final Activity activity, final KeyboardView keyboardView) {
        View decorView = activity.getWindow().getDecorView();

        FrameLayout keyboardWrapper = decorView.findViewById(R.id.keyboard_wrapper_id);
        if (keyboardWrapper == null) {
            ViewParent keyboardViewParent = keyboardView.getParent();
            if (keyboardViewParent != null) {
                if (((View) keyboardViewParent).getId() == R.id.keyboard_wrapper_id
                        && keyboardViewParent instanceof FrameLayout) {
                    keyboardWrapper = (FrameLayout) keyboardViewParent;
                    makeSureHasNoParent(keyboardWrapper);
                }
            }
            if (keyboardWrapper == null) {
                keyboardWrapper = wrapKeyboardView(activity, keyboardView);
            }

            if (decorView instanceof FrameLayout) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.BOTTOM;
                params.bottomMargin = getNavigationBarHeight(activity);
                ((ViewGroup) decorView).addView(keyboardWrapper, params);
            }
            return true;
        } else {
            keyboardWrapper.setVisibility(View.VISIBLE);
            keyboardWrapper.bringToFront();
            return false;
        }
    }

    @NonNull
    private static FrameLayout wrapKeyboardView(Activity activity, KeyboardView keyboardView) {
        FrameLayout keyboardWrapper = new FrameLayout(activity);
        keyboardWrapper.setId(R.id.keyboard_wrapper_id);
        keyboardWrapper.setClipChildren(false);

        FrameLayout.LayoutParams keyboardParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        keyboardWrapper.addView(keyboardView, keyboardParams);
        return keyboardWrapper;
    }

    private static int getNavigationBarHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int appUsableHeight = size.y;

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);

        } else {
            try {
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception e) {
                return 0;
            }
        }
        return size.y - appUsableHeight;
    }

    private static void makeSureHasNoParent(View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static boolean dismissFromActivity(Activity activity) {
        View view = activity.getWindow().getDecorView().findViewById(R.id.keyboard_wrapper_id);
        if (view == null) {
            return false;
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
            return true;
        }
    }
}
