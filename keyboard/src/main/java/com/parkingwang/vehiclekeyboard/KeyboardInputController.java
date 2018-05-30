package com.parkingwang.vehiclekeyboard;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parkingwang.vehiclekeyboard.core.KeyboardEntry;
import com.parkingwang.vehiclekeyboard.core.NumberType;
import com.parkingwang.vehiclekeyboard.support.Objects;
import com.parkingwang.vehiclekeyboard.support.Texts;
import com.parkingwang.vehiclekeyboard.view.InputView;
import com.parkingwang.vehiclekeyboard.view.KeyboardView;
import com.parkingwang.vehiclekeyboard.view.OnKeyboardChangedListener;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @author 陈永佳 (chenyongjia@parkingwang.com)
 */
public class KeyboardInputController {

    private static final String TAG = "KeyboardInputController";

    private final KeyboardView mKeyboardView;
    private final InputView mInputView;

    private final Set<OnInputChangedListener> mOnInputChangedListeners = new LinkedHashSet<>(4);

    private boolean mLockedOnNewEnergyType = false;
    private boolean mDebugEnabled = true;
    private MessageHandler mMessageHandler;

    /**
     * 使用键盘View和输入View，创建键盘输入控制器
     *
     * @param keyboardView 键盘View
     * @param inputView    输入框View
     * @return KeyboardInputController
     */
    public static KeyboardInputController with(KeyboardView keyboardView, InputView inputView) {
        return new KeyboardInputController(keyboardView, inputView);
    }

    /**
     * 使用键盘View和输入View，创建键盘输入控制器
     *
     * @param keyboardView 键盘View
     * @param inputView    输入框View
     */
    public KeyboardInputController(KeyboardView keyboardView, InputView inputView) {
        mKeyboardView = keyboardView;
        mInputView = inputView;
        // 绑定输入框被选中的触发事件：更新键盘
        mInputView.addOnFieldViewSelectedListener(new InputView.OnFieldViewSelectedListener() {
            @Override
            public void onSelectedAt(int index) {
                final String number = mInputView.getNumber();
                if (mDebugEnabled) {
                    Log.w(TAG, "点击输入框更新键盘, 号码：" + number + "，序号：" + index);
                }
                // 除非锁定新能源类型，否则都让引擎自己检测车牌类型
                if (mLockedOnNewEnergyType) {
                    mKeyboardView.update(number, index, NumberType.NEW_ENERGY);
                } else {
                    mKeyboardView.update(number, index, NumberType.AUTO_DETECT);
                }
            }
        });

        // 绑定键盘按键点击事件：更新输入框字符操作，输入框长度变化
        mKeyboardView.addKeyboardChangedListener(syncKeyboardInputState());
        // 检测键盘更新，尝试自动提交只有一位文本按键的操作
//        mKeyboardView.addKeyboardChangedListener(new AutoCommit(mInputView));
        // 触发键盘更新回调
        mKeyboardView.addKeyboardChangedListener(triggerInputChangedCallback());
    }

    /**
     * 绑定新能源车牌类型锁定按钮实现接口。
     * 当键盘切换新能源车牌时，会调用此接口相关函数来更改锁定按钮状态。
     *
     * @param proxy 锁定按钮代理实现接口
     * @return KeyboardInputController
     */
    public KeyboardInputController bindLockTypeProxy(final LockNewEnergyProxy proxy) {
        // 点击按钮时，切换新能源车牌绑定状态
        proxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLockNewEnergyType(!mLockedOnNewEnergyType);
            }
        });
        // 新能源车牌绑定状态，同步键盘更新的新能源类型
        mKeyboardView.addKeyboardChangedListener(new OnKeyboardChangedListener.Simple() {
            @Override
            public void onKeyboardChanged(KeyboardEntry keyboard) {
                // 如果键盘更新当前为新能源类型时，强制锁定为新能源类型
                if (NumberType.NEW_ENERGY.equals(keyboard.currentNumberType)) {
                    tryLockNewEnergyType(true);
                }
                // 同步锁定按钮
                proxy.onNumberTypeChanged(NumberType.NEW_ENERGY.equals(keyboard.currentNumberType));
            }
        });
        return this;
    }

    /**
     * 使用默认Toast的消息显示处理接口。
     * 默认时，键盘状态切换的提示消息，通过Toast接口来显示。
     *
     * @return KeyboardBinder
     */
    public KeyboardInputController useDefaultMessageHandler() {
        return setMessageHandler(new MessageHandler() {
            @Override
            public void onMessageError(int message) {
                Toast.makeText(mKeyboardView.getContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageTip(int message) {
                Toast.makeText(mKeyboardView.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更新输入组件的车牌号码，并默认选中最后编辑位。
     *
     * @param number 车牌号码
     */
    public void updateNumber(String number) {
        updateNumberLockType(number, false);
    }

    /**
     * 更新输入组件的车牌号码，指定是否锁定新能源类型，并默认选中最后编辑位。
     *
     * @param number                车牌号码
     * @param lockedOnNewEnergyType 是否锁定为新能源类型
     */
    public void updateNumberLockType(String number, boolean lockedOnNewEnergyType) {
        final String newNumber = number == null ? "" : number;
        mLockedOnNewEnergyType = lockedOnNewEnergyType;
        mInputView.updateNumber(newNumber);
        mInputView.performLastPendingFieldView();
    }

    ////

    /**
     * 设置键盘提示消息回调接口
     *
     * @param handler 消息回调接口
     * @return KeyboardBinder
     */
    public KeyboardInputController setMessageHandler(MessageHandler handler) {
        mMessageHandler = Objects.notNull(handler);
        return this;
    }

    /**
     * 添加输入变更回调接口
     *
     * @param listener 回调接口
     * @return KeyboardInputController
     */
    public KeyboardInputController addOnInputChangedListener(OnInputChangedListener listener) {
        mOnInputChangedListeners.add(Objects.notNull(listener));
        return this;
    }

    /**
     * 移除输入变更回调接口
     *
     * @param listener 回调接口
     * @return KeyboardInputController
     */
    public KeyboardInputController removeOnInputChangedListener(OnInputChangedListener listener) {
        mOnInputChangedListeners.remove(Objects.notNull(listener));
        return this;
    }

    /**
     * 设置是否启用调试信息
     *
     * @param enabled 是否启用
     * @return KeyboardInputController
     */
    public KeyboardInputController setDebugEnabled(boolean enabled) {
        mDebugEnabled = enabled;
        return this;
    }

    //////

    private void updateInputViewItemsByNumberType(NumberType type) {
        // 如果检测到的车牌号码为新能源、地方武警，需要显示第8位车牌
        final boolean show;
        if (NumberType.NEW_ENERGY.equals(type) || NumberType.WJ2012.equals(type) || mLockedOnNewEnergyType) {
            show = true;
        } else {
            show = false;
        }
        mInputView.set8thVisibility(show);
    }

    private void tryLockNewEnergyType(boolean toLock) {
        // not changed
        if (toLock == mLockedOnNewEnergyType) {
            return;
        }
        final boolean completed = mInputView.isCompleted();
        if (toLock) {
            triggerLockEnergyType(completed);
        } else {// unlock
            triggerUnlockEnergy(completed);
        }
    }

    // 解锁新能源车牌
    private void triggerUnlockEnergy(boolean completed) {
        mLockedOnNewEnergyType = false;
        mMessageHandler.onMessageTip(R.string.pwk_now_is_normal);
        final boolean lastItemSelected = mInputView.isLastFieldViewSelected();
        updateInputViewItemsByNumberType(NumberType.AUTO_DETECT);
        if (completed || lastItemSelected) {
            mInputView.performLastPendingFieldView();
        } else {
            mInputView.performCurrentFieldView();
        }
    }

    // 锁定新能源车牌
    private void triggerLockEnergyType(boolean completed) {
        if (Texts.isNewEnergyType(mInputView.getNumber())) {
            mLockedOnNewEnergyType = true;
            mMessageHandler.onMessageTip(R.string.pwk_now_is_energy);
            updateInputViewItemsByNumberType(NumberType.NEW_ENERGY);
            if (completed) {
                mInputView.performNextFieldView();
            } else {
                mInputView.performCurrentFieldView();
            }
        } else {
            mMessageHandler.onMessageError(R.string.pwk_change_to_energy_disallow);
        }
    }

    // 输入变更回调
    private OnKeyboardChangedListener triggerInputChangedCallback() {
        return new OnKeyboardChangedListener.Simple() {
            @Override
            public void onTextKey(String text) {
                notifyChanged();
            }

            @Override
            public void onDeleteKey() {
                notifyChanged();
            }

            @Override
            public void onConfirmKey() {
                final String number = mInputView.getNumber();
                for (OnInputChangedListener listener : mOnInputChangedListeners) {
                    listener.onCompleted(number, false);
                }
            }

            private void notifyChanged() {
                final boolean completed = mInputView.isCompleted();
                final String number = mInputView.getNumber();
                try {
                    for (OnInputChangedListener listener : mOnInputChangedListeners) {
                        listener.onChanged(number, completed);
                    }
                } finally {
                    if (completed) {
                        for (OnInputChangedListener listener : mOnInputChangedListeners) {
                            listener.onCompleted(number, true);
                        }
                    }
                }
            }
        };
    }

    private OnKeyboardChangedListener syncKeyboardInputState() {
        return new OnKeyboardChangedListener.Simple() {
            @Override
            public void onTextKey(String text) {
                mInputView.updateSelectedCharAndSelectNext(text);
            }

            @Override
            public void onDeleteKey() {
                mInputView.removeLastCharOfNumber();
            }

            @Override
            public void onKeyboardChanged(KeyboardEntry keyboard) {
                if (mDebugEnabled) {
                    Log.w(TAG, "键盘已更新，" +
                            "预设号码号码：" + keyboard.presetNumber +
                            "，最终探测类型：" + keyboard.currentNumberType
                    );
                }
                updateInputViewItemsByNumberType(keyboard.currentNumberType);
            }
        };
    }

    /**
     * 锁定车牌类型代理接口
     */
    public interface LockNewEnergyProxy {

        /**
         * 设置点击切换车牌类型的点击回调接口。通常使用Button来实现。
         *
         * @param listener 点击回调接口。
         */
        void setOnClickListener(View.OnClickListener listener);

        /**
         * 当车牌类型发生变化时，此方法被回调。
         *
         * @param isNewEnergyType 当前是否为新能源类型
         */
        void onNumberTypeChanged(boolean isNewEnergyType);
    }

    @Deprecated
    public interface LockTypeProxy extends LockNewEnergyProxy {
    }

    /**
     * 使用Button组件实现的锁定新能源车牌切换逻辑
     */
    public static class ButtonProxyImpl implements LockNewEnergyProxy {

        private final Button mButton;

        public ButtonProxyImpl(Button button) {
            mButton = button;
        }

        @Override
        public void setOnClickListener(View.OnClickListener listener) {
            mButton.setOnClickListener(listener);
        }

        @Override
        public void onNumberTypeChanged(boolean isNewEnergyType) {
            if (isNewEnergyType) {
                mButton.setText(R.string.pwk_change_to_normal);
            } else {
                mButton.setText(R.string.pwk_change_to_energy);
            }
        }
    }

    @Deprecated
    public static class ButtonProxy extends ButtonProxyImpl {

        public ButtonProxy(Button button) {
            super(button);
        }
    }
}
