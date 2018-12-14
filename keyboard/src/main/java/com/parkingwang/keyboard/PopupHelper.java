package com.parkingwang.keyboard;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
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
        return showToWindow(activity.getWindow(), keyboardView, false);
    }

    public static boolean showToWindow(final Window window, final KeyboardView keyboardView) {
        return showToWindow(window, keyboardView, false);
    }

    public static boolean showToWindow(final Window window, final KeyboardView keyboardView, boolean belowFirstView) {
        View rootView = window.getDecorView().findViewById(android.R.id.content);

        FrameLayout keyboardWrapper = rootView.findViewById(R.id.keyboard_wrapper_id);
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
                keyboardWrapper = wrapKeyboardView(keyboardView.getContext(), keyboardView);
            }

            if (rootView instanceof FrameLayout) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.BOTTOM;
                if (belowFirstView) {
                    params.topMargin = ((ViewGroup) rootView).getChildAt(0).getHeight();
                }
                ((ViewGroup) rootView).addView(keyboardWrapper, params);
            }
            return true;
        } else {
            keyboardWrapper.setVisibility(View.VISIBLE);
            keyboardWrapper.bringToFront();
            return false;
        }
    }

    @NonNull
    private static FrameLayout wrapKeyboardView(Context context, KeyboardView keyboardView) {
        FrameLayout keyboardWrapper = new FrameLayout(context);
        keyboardWrapper.setId(R.id.keyboard_wrapper_id);
        keyboardWrapper.setClipChildren(false);

        FrameLayout.LayoutParams keyboardParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        keyboardWrapper.addView(keyboardView, keyboardParams);
        return keyboardWrapper;
    }

    private static void makeSureHasNoParent(View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static boolean dismissFromActivity(Activity activity) {
        return dismissFromWindow(activity.getWindow());
    }

    public static boolean dismissFromWindow(Window window) {
        View view = window.getDecorView().findViewById(R.id.keyboard_wrapper_id);
        if (view == null) {
            return false;
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
            return true;
        }
    }
}
