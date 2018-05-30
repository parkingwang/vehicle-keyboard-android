package com.parkingwang.vehiclekeyboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.parkingwang.vehiclekeyboard.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class InputView extends LinearLayout {

    private static final String KEY_INIT_NUMBER = "pwk.keyboard.key:init.number";

    public static final int IDX_L7_END = 7;
    public static final int IDX_L7_REPLACE = 6;
    public static final int IDX_L8_END = 8;

    private final Button[] mFieldViews = new Button[9];
    private final HashMap<String, Object> mKeyMap = new HashMap<>();

    private final Set<OnFieldViewSelectedListener> mOnFieldViewSelectedListeners = new HashSet<>(4);

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
            final ClickMetas clickMetas = getClickedMeta((Button) v);
            if (clickMetas.clickIndex <= clickMetas.numberLength) {
                // 更新选中状态
                if (clickMetas.clickIndex != clickMetas.selectedIndex) {
                    if (clickMetas.selectedIndex >= 0) {
                        clearSelectedState(mFieldViews[clickMetas.selectedIndex]);
                    }
                    setFieldSelected(mFieldViews[clickMetas.clickIndex]);
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
        init(context, attrs);
    }

    public InputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.pwk_input_view, this);
        // Input Cell字体大小
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InputView);
        final float textSize = ta.getDimension(R.styleable.InputView_pwkInputTextSize, 0);
        ta.recycle();

        final int[] resIds = new int[]{
                R.id.number_0, R.id.number_1, R.id.number_2,
                R.id.number_3, R.id.number_4, R.id.number_5,
                R.id.number_6,
                R.id.number_6_of_end,
                R.id.number_7_of_end
        };

        final boolean textSizeDefined = textSize > 0;
        for (int i = 0; i < mFieldViews.length; i++) {
            mFieldViews[i] = findViewById(resIds[i]);
            mFieldViews[i].setOnClickListener(mOnFieldViewClickListener);
            if (textSizeDefined) {
                mFieldViews[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        }
        // 最后一位默认隐藏
        set8thFieldViewVisibility(false, true);
    }

    /**
     * 设置文本字符到当前选中的输入框
     *
     * @param text 文本字符
     */
    public void updateSelectedCharAndSelectNext(final String text) {
        withFields()
                .filter(new Predicate<Button>() {
                    @Override
                    public boolean test(Button view) {
                        return view.isSelected();
                    }
                })
                .findSingle()
                .ifPresent(new Consumer<Button>() {
                    @Override
                    public void accept(Button button) {
                        button.setText(text);
                        // 跳转到下一位
                        performNextFieldViewBy(button);
                    }
                });
    }

    /**
     * 从最后一位开始删除
     */
    public void removeLastCharOfNumber() {
        for (int i = mFieldViews.length - 1; i >= 0; i--) {
            final Button field = mFieldViews[i];
            if (!field.isShown()) {
                field.setText(null);
            } else if (!TextUtils.isEmpty(field.getText())) {
                field.setText(null);
                performFieldViewSetToSelected(field);
                break;
            }
        }
    }

    /**
     * @return 返回当前输入组件是否为完成状态
     */
    public boolean isCompleted() {
        // 所有显示的输入框都被填充了车牌号码，即输入完成状态
        return withFields().allMatch(checkIfNotEmpty());
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
        final char[] chars = number.toCharArray();
        // check 8th input item show
        final boolean charsOf8 = chars.length >= 8;
        set8thFieldViewVisibility(charsOf8, !charsOf8);
        // 显示到对应键位
        for (int i = 0; i < mFieldViews.length; i++) {
            final String text;
            if (i < chars.length) {
                text = String.valueOf(chars[i]);
            } else {
                text = null;
            }
            mFieldViews[i].setText(text);
        }
    }

    /**
     * 获取当前已输入的车牌号码
     *
     * @return 车牌号码
     */
    public String getNumber() {
        return withFields()
                .map(new Function<Button, String>() {
                    @Override
                    public String apply(Button textView) {
                        return textView.getText().toString();
                    }
                }).reduce("", new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String char1, String char2) {
                        return char1 + char2;
                    }
                });
    }

    /**
     * 选中第一个输入框
     */
    public void performFirstFieldView() {
        performFieldViewSetToSelected(mFieldViews[0]);
    }

    /**
     * 选中最后一个可等待输入的输入框。
     * 如果全部为空，则选中第1个输入框。
     */
    public void performLastFieldView() {
        findLastFilledTextFieldView().ifPresentOrElse(new Consumer<Button>() {
            @Override
            public void accept(Button button) {
                performNextFieldViewBy(button);
            }
        }, new Runnable() {
            @Override
            public void run() {
                performFieldViewSetToSelected(mFieldViews[0]);
            }
        });
    }

    /**
     * 选中下一个输入框
     */
    public void performNextFieldView() {
        final ClickMetas clickMetas = getClickedMeta(null);
        if (clickMetas.selectedIndex >= 0) {
            performNextFieldViewBy(mFieldViews[clickMetas.selectedIndex]);
        }
    }

    /**
     * 重新触发当前输入框选中状态
     */
    public void performCurrentFieldView() {
        final ClickMetas clickMetas = getClickedMeta(null);
        if (clickMetas.selectedIndex >= 0) {
            performFieldViewSetToSelected(mFieldViews[clickMetas.selectedIndex]);
        }
    }

    /**
     * 设置第8位输入框显示状态，并设置是否清空其文本
     *
     * @param setToShow8thField 是否显示
     * @param clearText 是否清空其文本内容
     */
    public void set8thFieldViewVisibility(boolean setToShow8thField, boolean clearText) {
        final Button button8th = mFieldViews[IDX_L8_END];
        final boolean showing = button8th.isShown();
        // 如果要显示第8位，则：
        // - 替换位要隐藏；
        // - 第8位显示
        if (setToShow8thField) {
            if (!showing) {
                mFieldViews[IDX_L7_REPLACE].setVisibility(GONE);
                button8th.setVisibility(VISIBLE);
            }
        } else {
            if (showing) {
                mFieldViews[IDX_L7_REPLACE].setVisibility(VISIBLE);
                button8th.setVisibility(GONE);
            }
        }
        if (clearText && !isButtonEmpty(button8th)) {
            button8th.setText(null);
        }
    }

    /**
     * 是否最后一位被选中状态。
     *
     * @return 是否选中
     */
    public boolean isLastFieldViewSelected() {
        if (mFieldViews[IDX_L8_END].isShown()) {
            return mFieldViews[IDX_L8_END].isSelected();
        } else {
            return mFieldViews[IDX_L7_END].isSelected();
        }
    }

    public InputView addOnFieldViewSelectedListener(OnFieldViewSelectedListener listener) {
        mOnFieldViewSelectedListeners.add(listener);
        return this;
    }

    private void performFieldViewSetToSelected(Button target) {
        // target.performClick();
        // 自动触发的，不要使用Android内部处理，太慢了。
        mOnFieldViewClickListener.onClick(target);
        setFieldSelected(target);
    }

    private void performNextFieldViewBy(Button current) {
        final List<Button> buttons = withFields().collect(Collectors.<Button>toList());
        final int nextIndex = buttons.lastIndexOf(current) + 1;
        final int clickIndex = Math.min(nextIndex, buttons.size() - 1);
        performFieldViewSetToSelected(buttons.get(clickIndex));
    }

    private Optional<Button> findLastFilledTextFieldView() {
        final List<Button> reverse = new ArrayList<>(Arrays.asList(mFieldViews));
        Collections.reverse(reverse);
        return Stream.of(reverse)
                .filter(checkIfShown())
                .filter(checkIfNotEmpty())
                .findFirst();
    }

    private void clearSelectedState(Button target) {
        target.setSelected(false);
    }

    private void setFieldSelected(Button target) {
        for (Button btn : mFieldViews) {
            btn.setSelected((btn == target));
        }
    }

    private Stream<Button> withFields() {
        return Stream.of(mFieldViews)
                .filter(checkIfShown());
    }

    private Predicate<Button> checkIfShown() {
        return new Predicate<Button>() {
            @Override
            public boolean test(Button value) {
                return value.isShown();
            }
        };
    }

    private Predicate<Button> checkIfNotEmpty() {
        return new Predicate<Button>() {
            @Override
            public boolean test(Button button) {
                return !isButtonEmpty(button);
            }
        };
    }

    private ClickMetas getClickedMeta(Button clicked) {
        short selected = -1;
        short current = 0;
        short length = 0;
        for (int i = 0; i < mFieldViews.length; i++) {
            final Button item = mFieldViews[i];
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
