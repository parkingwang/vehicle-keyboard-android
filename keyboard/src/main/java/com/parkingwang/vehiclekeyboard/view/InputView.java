package com.parkingwang.vehiclekeyboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.parkingwang.vehiclekeyboard.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class InputView extends LinearLayout {

    private static final String TAG = InputView.class.getName();

    private static final String KEY_INIT_NUMBER = "pwk.keyboard.key:init.number";

    private final HashMap<String, Object> mKeyMap = new HashMap<>();

    private final Set<OnFieldViewSelectedListener> mOnFieldViewSelectedListeners = new HashSet<>(4);

    private final ButtonGroup mButtonGroup;

    /**
     * 输入框被点击时，有以下逻辑：
     * <p>
     * 1. 检查当前输入框是否可以被选中。可选中条件是：
     * - 选中序号为0，任何时候都可以被选中；
     * - 序号大于0，最大可点击序号为当前车牌长度；
     * <p>
     * 2. 清除另一个被选中状态，设置当前为选中状态；
     * <p>
     * 3. 触发选中回调；
     */
    private final OnClickListener mOnFieldViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Click Handler: ----");
            final ClickMetas clickMetas = getClickedMeta((Button) v);
            if (clickMetas.clickIndex <= clickMetas.numberLength) {
                // 更新选中状态
                if (clickMetas.clickIndex != clickMetas.selectedIndex) {
                    if (clickMetas.selectedIndex >= 0) {
                        clearSelectedState(mButtonGroup.getFieldViewAt(clickMetas.selectedIndex));
                    }
                    Log.d(TAG, "当前点击序号: " + clickMetas.clickIndex);
                    setFieldViewSelected(mButtonGroup.getFieldViewAt(clickMetas.clickIndex));
                }
                // 触发选中回调
                for (OnFieldViewSelectedListener listener : mOnFieldViewSelectedListeners) {
                    listener.onSelectedAt(clickMetas.clickIndex);
                }
            }
        }
    };

    public InputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.pwk_input_view, this);
        mButtonGroup = new ButtonGroup() {
            @Override
            protected Button findViewById(int id) {
                return InputView.this.findViewById(id);
            }
        };
        onInited(context, attrs);
    }

    public InputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.pwk_input_view, this);
        mButtonGroup = new ButtonGroup() {
            @Override
            protected Button findViewById(int id) {
                return InputView.this.findViewById(id);
            }
        };
        onInited(context, attrs);
    }

    private void onInited(Context context, AttributeSet attrs) {
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InputView);
        final float textSize = ta.getDimension(R.styleable.InputView_pwkInputTextSize, 0);
        ta.recycle();
        mButtonGroup.setAllFieldViewsTextSize(textSize);
        mButtonGroup.setAllFieldViewsOnClickListener(mOnFieldViewClickListener);
        mButtonGroup.changeTo7FieldViews();
    }

    /**
     * 设置文本字符到当前选中的输入框
     *
     * @param text 文本字符
     */
    public void updateSelectedCharAndSelectNext(final String text) {
        final Button selected = mButtonGroup.getFirstSelectedOrNull();
        if (selected != null) {
            selected.setText(text);
            performNextFieldViewBy(selected);
        }
    }

    /**
     * 从最后一位开始删除
     */
    public void removeLastCharOfNumber() {
        final Button last = mButtonGroup.getLastFilledOrNull();
        if (last != null) {
            last.setText(null);
            performFieldViewSetToSelected(last);
        }
    }

    /**
     * @return 返回当前输入组件是否为完成状态
     */
    public boolean isCompleted() {
        // 所有显示的输入框都被填充了车牌号码，即输入完成状态
        return mButtonGroup.isAllFilled();
    }

    /**
     * 返回当前车牌号码是否被修改过。
     * 与通过 updateNumber 方法设置的车牌号码来对比。
     *
     * @return 是否修改过
     */
    public boolean isNumberChanged() {
        final String current = getNumber();
        return !current.equals(String.valueOf(mKeyMap.get(KEY_INIT_NUMBER)));
    }

    /**
     * 更新/重置车牌号码
     *
     * @param number 车牌号码
     */
    public void updateNumber(String number) {
        // 初始化车牌
        mKeyMap.put(KEY_INIT_NUMBER, number);
        mButtonGroup.setTextToFields(number);
    }

    /**
     * 获取当前已输入的车牌号码
     *
     * @return 车牌号码
     */
    public String getNumber() {
        return mButtonGroup.getText();
    }

    /**
     * 选中第一个输入框
     */
    public void performFirstFieldView() {
        performFieldViewSetToSelected(mButtonGroup.getFieldViewAt(0));
    }

    /**
     * 选中最后一个可等待输入的输入框。
     * 如果全部为空，则选中第1个输入框。
     */
    public void performLastPendingFieldView() {
        final Button field = mButtonGroup.getLastFilledOrNull();
        if (field != null) {
            performNextFieldViewBy(field);
        } else {
            performFieldViewSetToSelected(mButtonGroup.getFieldViewAt(0));
        }
    }

    /**
     * 选中下一个输入框。
     * 如果当前输入框是空字符，则重新触发当前输入框的点击事件。
     */
    public void performNextFieldView() {
        final ClickMetas clickMetas = getClickedMeta(null);
        if (clickMetas.selectedIndex >= 0) {
            final Button current = mButtonGroup.getFieldViewAt(clickMetas.selectedIndex);
            if (!TextUtils.isEmpty(current.getText())) {
                performNextFieldViewBy(current);
            } else {
                performFieldViewSetToSelected(current);
            }
        }
    }

    /**
     * 重新触发当前输入框选中状态
     */
    public void rePerformCurrentFieldView() {
        final ClickMetas clickMetas = getClickedMeta(null);
        if (clickMetas.selectedIndex >= 0) {
            performFieldViewSetToSelected(mButtonGroup.getFieldViewAt(clickMetas.selectedIndex));
        }
    }

    /**
     * 设置第8位输入框显示状态
     *
     * @param setToShow8thField 是否显示
     */
    public void set8thVisibility(boolean setToShow8thField) {
        final boolean changed;
        if (setToShow8thField) {
            changed = mButtonGroup.changeTo8FieldViews();
        } else {
            changed = mButtonGroup.changeTo7FieldViews();
        }
        if (changed) {
            final Button field = mButtonGroup.getFirstEmpty();
            if (field != null) {
                Log.d(TAG, "[@@ FieldChanged @@] FirstEmpty.tag: " + field.getTag());
                setFieldViewSelected(field);
            }
        }
    }

    /**
     * 是否最后一位被选中状态。
     *
     * @return 是否选中
     */
    public boolean isLastFieldViewSelected() {
        return mButtonGroup.getLastFieldView().isSelected();
    }

    public InputView addOnFieldViewSelectedListener(OnFieldViewSelectedListener listener) {
        mOnFieldViewSelectedListeners.add(listener);
        return this;
    }

    private void performFieldViewSetToSelected(Button target) {
        Log.d(TAG, "[== FastPerform ==] Btn.text: " + target.getText());
        // target.performClick();
        // 自动触发的，不要使用Android内部处理，太慢了。
        mOnFieldViewClickListener.onClick(target);
        setFieldViewSelected(target);
    }

    private void performNextFieldViewBy(Button current) {
        final int nextIndex = mButtonGroup.getNextIndexOf(current);
        Log.d(TAG, "[>> NextPerform >>] Next.Btn.idx: " + nextIndex);
        performFieldViewSetToSelected(mButtonGroup.getFieldViewAt(nextIndex));
    }

    private void clearSelectedState(Button target) {
        target.setSelected(false);
    }

    private void setFieldViewSelected(Button target) {
        for (Button btn : mButtonGroup.getAvailableFieldViews()) {
            btn.setSelected((btn == target));
        }
    }

    private ClickMetas getClickedMeta(Button clicked) {
        short selected = -1;
        short current = 0;
        short length = 0;
        final Button[] fields = mButtonGroup.getAvailableFieldViews();
        for (int i = 0; i < fields.length; i++) {
            final Button item = fields[i];
            if (item == clicked) {
                current = (short) i;
            }
            if (item.isSelected()) {
                selected = (short) i;
            }
            if (!isButtonEmpty(item)) {
                length += 1;
            }
        }
        return new ClickMetas(selected, current, length);
    }

    private static boolean isButtonEmpty(Button button) {
        return button.getText().length() == 0;
    }

    //////

    private static class ClickMetas {

        final short selectedIndex;
        final short clickIndex;
        final short numberLength;

        private ClickMetas(short selectedIndex, short clickIndex, short numberLength) {
            this.selectedIndex = selectedIndex;
            this.clickIndex = clickIndex;
            this.numberLength = numberLength;
        }
    }

    //////////

    public interface OnFieldViewSelectedListener {

        void onSelectedAt(int index);
    }

}
