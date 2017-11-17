package com.parkingwang.vehiclekeyboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
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

    private Button mButtonKeyOf6;
    private Button mButtonEndOf6;

    private final Button[] mButtonItems = new Button[8];
    private final HashMap<String, Object> mKeyMap = new HashMap<>();

    private final Set<OnItemSelectedListener> mOnItemSelectedListeners =
            new HashSet<>(4);

    /**
     * 输入框被点击时，有以下逻辑：
     *
     * 1. 检查当前输入框是否可以被选中。可选中条件是：
     *  - 选中序号为0，任何时候都可以被选中；
     *  - 序号大于0，最大可点击序号为当前车牌长度；
     *
     * 2. 清除另一个被选中状态，设置当前为选中状态；
     *
     * 3. 触发选中回调；
     */
    private final OnClickListener mOnButtonItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Meta meta = getClickedMeta((Button) v);
            if (meta.clickIndex <= meta.numberLength) {
                // 更新选中状态
                if (meta.clickIndex != meta.selectedIndex) {
                    if (meta.selectedIndex >= 0) {
                        clearSelectedState(mButtonItems[meta.selectedIndex]);
                    }
                    setSelectedState(mButtonItems[meta.clickIndex]);
                }
                // 触发选中回调
                for (OnItemSelectedListener listener : mOnItemSelectedListeners) {
                    listener.onSelected(meta.clickIndex);
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
                R.id.number_6, R.id.number_7,
        };

        mButtonKeyOf6 = (Button) findViewById(R.id.number_6);
        mButtonEndOf6 = (Button) findViewById(R.id.number_6_as_end);
        mButtonEndOf6.setOnClickListener(mOnButtonItemClickListener);
        final boolean textSizeDefined = textSize > 0;
        if (textSizeDefined) {
            mButtonEndOf6.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        for (int i = 0; i < mButtonItems.length; i++) {
            mButtonItems[i] = (Button) findViewById(resIds[i]);
            mButtonItems[i].setOnClickListener(mOnButtonItemClickListener);
            if (textSizeDefined) {
                mButtonItems[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        }
        // 最后一位默认隐藏
        set8thItemVisibility(false, true);
    }

    /**
     * 设置文本字符到当前选中的输入框
     *
     * @param text 文本字符
     */
    public void updateSelectedCharAndSelectNext(final String text) {
        streamOfShownItems()
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
                        performNextItemBy(button);
                    }
                });
    }

    /**
     * 从最后一位开始删除
     */
    public void removeLastCharOfNumber() {
        findLastFilledTextItem()
                .ifPresent(new Consumer<Button>() {
                    @Override
                    public void accept(Button cell) {
                        cell.setText(null);
                        cell.performClick();
                    }
                });
    }

    /**
     * @return 返回当前输入组件是否为完成状态
     */
    public boolean isCompleted() {
        // 所有显示的输入框都被填充了车牌号码，即输入完成状态
        return streamOfShownItems()
                .allMatch(notEmpty());
    }

    /**
     * 返回当前车牌号码是否被修改过。
     * 与通过 updateNumber 方法设置的车牌号码来对比。
     * @return 是否修改过
     */
    public boolean isNumberChanged() {
        String current = getNumber();
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
        set8thItemVisibility(charsOf8, !charsOf8);
        // 显示到对应键位
        // reset first
        mButtonEndOf6.setText(null);
        mButtonKeyOf6.setText(null);
        // setup text
        for (int i = 0; i < mButtonItems.length; i++) {
            final String text;
            if (i < chars.length) {
                text = String.valueOf(chars[i]);
            } else {
                text = null;
            }
            mButtonItems[i].setText(text);
        }
    }

    /**
     * 获取当前已输入的车牌号码
     *
     * @return 车牌号码
     */
    public String getNumber() {
        return streamOfShownItems()
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
    public void performFirstItem() {
        performItemSelected(mButtonItems[0]);
    }

    /**
     * 选中最后一个可等待输入的输入框。
     * 如果全部为空，则选中第1个输入框。
     */
    public void performLastItem() {
        findLastFilledTextItem().ifPresentOrElse(new Consumer<Button>() {
            @Override
            public void accept(Button button) {
                performNextItemBy(button);
            }
        }, new Runnable() {
            @Override
            public void run() {
                performItemSelected(mButtonItems[0]);
            }
        });
    }

    /**
     * 选中下一个输入框
     */
    public void performNextItem() {
        final Meta meta = getClickedMeta(null);
        if (meta.selectedIndex >= 0) {
            performNextItemBy(mButtonItems[meta.selectedIndex]);
        }
    }

    /**
     * 重新触发当前输入框选中状态
     */
    public void performCurrentItem() {
        final Meta meta = getClickedMeta(null);
        if (meta.selectedIndex >= 0) {
            performItemSelected(mButtonItems[meta.selectedIndex]);
        }
    }

    /**
     * 设置第8位输入框显示状态，并设置是否清空其文本
     * @param toShow 是否显示
     * @param clearText 是否清空其文本内容
     */
    public void set8thItemVisibility(boolean toShow, boolean clearText) {
        final Button button8th = mButtonItems[7];
        final boolean isShown = button8th.isShown();
        // 显示第8位
        if (toShow) {
            if (!isShown) {// 第8位未显示，则设置显示
                button8th.setVisibility(VISIBLE);
            }
            mButtonEndOf6.setVisibility(GONE);
            mButtonKeyOf6.setVisibility(VISIBLE);
            mButtonItems[6] = mButtonKeyOf6;
        }else{
            if (isShown) { // 隐藏第8位
                button8th.setVisibility(GONE);
            }
            mButtonEndOf6.setVisibility(VISIBLE);
            mButtonKeyOf6.setVisibility(GONE);
            mButtonItems[6] = mButtonEndOf6;
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
    public boolean isLastItemSelected() {
        if (mButtonItems[7].isShown()) {
            return mButtonItems[7].isSelected();
        } else {
            return mButtonItems[6].isSelected();
        }
    }

    public InputView addOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListeners.add(listener);
        return this;
    }

    private void performItemSelected(Button button) {
        button.performClick();
        setSelectedState(button);
    }

    private void performNextItemBy(Button cell) {
        final List<Button> buttons = streamOfShownItems()
                .collect(Collectors.<Button>toList());
        final int nextIndex = buttons.lastIndexOf(cell) + 1;
        final int clickIndex = Math.min(nextIndex, buttons.size() - 1);
        performItemSelected(buttons.get(clickIndex));
    }

    private Optional<Button> findLastFilledTextItem() {
        final List<Button> reverse = new ArrayList<>(Arrays.asList(mButtonItems));
        Collections.reverse(reverse);
        return Stream.of(reverse)
                .filter(shown())
                .filter(notEmpty())
                .findFirst();
    }

    private void clearSelectedState(Button item) {
        item.setSelected(false);
    }

    private void setSelectedState(Button cell) {
        for (Button btn : mButtonItems) {
            btn.setSelected((btn == cell));
        }
    }

    private Stream<Button> streamOfShownItems() {
        return Stream.of(mButtonItems)
                .filter(shown());
    }

    private Predicate<Button> shown() {
        return new Predicate<Button>() {
            @Override
            public boolean test(Button value) {
                return value.isShown();
            }
        };
    }

    private Predicate<Button> notEmpty() {
        return new Predicate<Button>() {
            @Override
            public boolean test(Button button) {
                return !isButtonEmpty(button);
            }
        };
    }

    private Meta getClickedMeta(Button clicked) {
        short selected = -1;
        short current = 0;
        short length = 0;
        for (int i = 0; i < mButtonItems.length; i++) {
            final Button item = mButtonItems[i];
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
        return new Meta(selected, current, length);
    }

    private static boolean isButtonEmpty(Button button) {
        return button.getText().length() == 0;
    }

    //////

    private static class Meta {

        final short selectedIndex;
        final short clickIndex;
        final short numberLength;

        private Meta(short selectedIndex, short clickIndex, short numberLength) {
            this.selectedIndex = selectedIndex;
            this.clickIndex = clickIndex;
            this.numberLength = numberLength;
        }
    }

    //////////

    public interface OnItemSelectedListener {

        void onSelected(int index);
    }

}
