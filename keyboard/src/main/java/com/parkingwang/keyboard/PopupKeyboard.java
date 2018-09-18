package com.parkingwang.keyboard;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;

import com.parkingwang.keyboard.engine.KeyboardEngine;
import com.parkingwang.keyboard.view.InputView;
import com.parkingwang.keyboard.view.KeyboardView;

import static com.parkingwang.keyboard.PopupHelper.dismissFromActivity;
import static com.parkingwang.keyboard.PopupHelper.showToActivity;

/**
 * @author Yoojia Chen (yoojiachen@gmail.com)
 * @version 2017-11-03 0.1
 * @since 2017-11-03 0.1
 */
public class PopupKeyboard {

    private final KeyboardView mKeyboardView;

    private KeyboardInputController mController;

    public PopupKeyboard(Context context) {
        mKeyboardView = new KeyboardView(context);
    }

    public PopupKeyboard(Context context, @ColorInt int bubbleTextColor, ColorStateList okKeyBackgroundColor) {
        mKeyboardView = new KeyboardView(context);
        mKeyboardView.setBubbleTextColor(bubbleTextColor);
        mKeyboardView.setOkKeyBackgroundColor(okKeyBackgroundColor);
    }

    public KeyboardView getKeyboardView() {
        return mKeyboardView;
    }

    public void attach(InputView inputView, final Activity activity) {
        if (mController == null) {
            mController = KeyboardInputController
                    .with(mKeyboardView, inputView);
            mController.useDefaultMessageHandler();

            inputView.addOnFieldViewSelectedListener(new InputView.OnFieldViewSelectedListener() {
                @Override
                public void onSelectedAt(int index) {
                    show(activity);
                }
            });
        }
    }

    public KeyboardInputController getController() {
        return checkAttachedController();
    }

    public KeyboardEngine getKeyboardEngine() {
        return mKeyboardView.getKeyboardEngine();
    }

    public void show(Activity activity) {
        checkAttachedController();
        showToActivity(activity, mKeyboardView);
    }

    public void dismiss(Activity activity) {
        checkAttachedController();
        dismissFromActivity(activity);
    }

    public boolean isShown() {
        return mKeyboardView.isShown();
    }

    private KeyboardInputController checkAttachedController() {
        if (mController == null) {
            throw new IllegalStateException("Try attach() first");
        }
        return mController;
    }

}
