package com.parkingwang.vehiclekeyboard;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.parkingwang.vehiclekeyboard.core.KeyEntry;
import com.parkingwang.vehiclekeyboard.core.KeyboardEntry;
import com.parkingwang.vehiclekeyboard.core.NumberType;
import com.parkingwang.vehiclekeyboard.view.InputView;
import com.parkingwang.vehiclekeyboard.view.KeyboardCallback;
import com.parkingwang.vehiclekeyboard.view.KeyboardView;

import java.util.List;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @since 2017-10-31 0.5
 */
public class KeyboardBinder {

    private final KeyboardView mKeyboardView;
    private final InputView mInputView;

    private OnInputChangedListener mOnInputChangedListener;

    // 监听键盘返回的变化
    private final KeyboardCallback mKeyboardCallback = new KeyboardCallback.Base() {

        private boolean mIsDeleteAction = false;

        @Override
        public void onTextKey(String text) {
            mIsDeleteAction = false;
            mInputView.setTextAndNext(text);
            onKeyboardChanged();
        }

        @Override
        public void onDeleteKey() {
            mIsDeleteAction = true;
            mInputView.popNumberChar();
            onKeyboardChanged();
        }

        @Override
        public void onConfirmKey() {
            super.onConfirmKey();
            if (mOnInputChangedListener != null) {
                mOnInputChangedListener.onCompleted(mInputView.getNumber(), false);
            }
        }

        @Override
        public void onKeyboardInfo(KeyboardEntry info) {
            // 当键盘更新导致车牌号码输入框长度变化时
            mInputView.updateNumberType(info.detectedNumberType);
            checkSingleKeyAutoCommit(info);
        }

        private void checkSingleKeyAutoCommit(KeyboardEntry info) {
            // 如果可点击键位只有一个，并且前一个操作不是删除键，则自动提交
            if ((1 == info.index || 6 == info.index) && !mIsDeleteAction) {
                final List<KeyEntry> keys = Stream.of(info.keyRows)
                        .flatMap(new Function<List<KeyEntry>, Stream<KeyEntry>>() {
                            @Override
                            public Stream<KeyEntry> apply(List<KeyEntry> entries) {
                                return Stream.of(entries);
                            }
                        }).filter(new Predicate<KeyEntry>() {
                            @Override
                            public boolean test(KeyEntry key) {
                                return !key.isFunKey && key.enabled;
                            }
                        }).collect(Collectors.<KeyEntry>toList());
                if (keys.size() == 1) {
                    onTextKey(keys.get(0).text);
                }
            }
        }

        private void onKeyboardChanged() {
            if (mOnInputChangedListener != null) {
                final boolean completed = mInputView.isCompleted();
                final String number = mInputView.getNumber();
                try {
                    mOnInputChangedListener.onChanged(number, completed);
                } finally {
                    if (completed) {
                        mOnInputChangedListener.onCompleted(number, true);
                    }
                }
            }
        }
    };

    private KeyboardBinder(KeyboardView keyboardView, InputView inputView) {
        mKeyboardView = keyboardView;
        mInputView = inputView;
    }

    public static KeyboardBinder with(KeyboardView keyboardView, InputView inputView) {
        return new KeyboardBinder(keyboardView, inputView);
    }

    /**
     * 使用默认Toast的消息显示处理接口
     *
     * @return KeyboardBinder
     */
    public KeyboardBinder useDefaultMessageListener() {
        return setOnShowMessageListener(new InputView.OnShowMessageListener() {
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
     * 设置键盘提示消息回调接口
     *
     * @param listener 消息回调接口
     * @return KeyboardBinder
     */
    public KeyboardBinder setOnShowMessageListener(InputView.OnShowMessageListener listener) {
        mInputView.setOnShowMessageListener(listener);
        return this;
    }

    /**
     * 将InputView依附到KeyboardView的状态变化中。
     *
     * @return KeyboardBinder
     */
    public KeyboardBinder attach() {
        // 选中一个输入框
        mInputView.addOnItemSelectedListener(new InputView.OnItemSelectedListener() {
            @Override
            public void onSelected(int index) {
                mKeyboardView.update(
                        mInputView.getNumber(),
                        index,
                        mInputView.getNumberType());
            }
        });
        // 修改输入框
        mInputView.addOnNumberTypeChangedListener(new InputView.OnNumberTypeChangedListener() {
            @Override
            public void onChanged(NumberType type) {
                mKeyboardView.update(
                        mInputView.getNumber(),
                        mInputView.getCurrentSelectedIndex(),
                        type);
            }
        });
        return this;
    }

    /**
     * 设置键盘组件状态与输入组件状态同步
     *
     * @return KeyboardBinder
     */
    public KeyboardBinder setKeyboardInputChangeSync() {
        mKeyboardView.addKeyboardCallback(mKeyboardCallback);
        return this;
    }

    /**
     * 设置文本类型的新能源车牌号码锁定按钮
     *
     * @param button 锁定按钮
     * @return KeyboardBinder
     */
    public KeyboardBinder setLockTypeButton(final Button button) {
        return setLockTypeButton(button, new InputView.OnNewEnergyTypeLockListener() {
            @Override
            public void onTypeLock(boolean locked) {
                if (locked) {
                    button.setText(R.string.pwk_change_to_normal);
                } else {
                    button.setText(R.string.pwk_change_to_energy);
                }
            }
        });
    }

    /**
     * 设置新能源车牌号码锁定按钮，并设置锁定回调接口
     *
     * @param button 锁定按钮
     * @return KeyboardBinder
     */
    public KeyboardBinder setLockTypeButton(final View button, InputView.OnNewEnergyTypeLockListener listener) {
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mInputView.setNewEnergyTypeLocked(!mInputView.isNewEnergyTypeLocked());
//                mInputView.toggleNewEnergyTypeLockState();
            }
        });
        mInputView.setOnNewEnergyTypeLockListener(listener);
        return this;
    }

    /**
     * 设置键盘变化回调接口
     *
     * @param listener 回调接口
     * @return KeyboardBinder
     */
    public KeyboardBinder setOnInputChangeListener(OnInputChangedListener listener) {
        mOnInputChangedListener = listener;
        return this;
    }

    /**
     * 重置车牌号码，并选中第一个输入框
     * @param number 车牌号码
     */
    public void updateNumberAndSelectFirstItem(String number) {
        final String newNumber = nonNullNumber(number);
        final KeyboardCallback callback = new KeyboardCallback.Base() {
            @Override
            public void onKeyboardInfo(KeyboardEntry keyboard) {
                mKeyboardView.removeKeyboardCallback(this);
                mInputView.selectFirstItem();
            }
        };
        mKeyboardView.addKeyboardCallback(callback);
        mKeyboardView.update(newNumber);
        mInputView.resetNumber(newNumber);
    }

    /**
     * 更新车牌号码，并选中最后一个输入框
     *
     * @param number 车牌号码
     */
    public void updateNumberAndSelectLastItem(String number) {
        final KeyboardCallback callback = new KeyboardCallback.Base() {
            @Override
            public void onKeyboardInfo(KeyboardEntry keyboard) {
                mKeyboardView.removeKeyboardCallback(this);
                mInputView.selectLastItem();
            }
        };
        mKeyboardView.addKeyboardCallback(callback);
        final String newNumber = nonNullNumber(number);
        mKeyboardView.update(newNumber);
        mInputView.resetNumber(newNumber);
    }

    private static String nonNullNumber(String number) {
        return null == number ? "" : number;
    }
}
