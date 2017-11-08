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
import com.parkingwang.vehiclekeyboard.core.NumberType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class InputView extends LinearLayout {

    private static final String KEY_INIT_NUMBER = "pwk.keyboard.key:init.number";

    private final Button[] mSplitItems = new Button[8];
    private final HashMap<String, Object> mKeyMap = new HashMap<>();

    private Set<OnItemSelectedListener> mOnItemSelectedListeners = new HashSet<>(2);
    private Set<OnNumberTypeChangedListener> mOnNumberTypeChangedListeners = new HashSet<>(2);

    private OnShowMessageListener mOnShowMessageListener;
    private OnNewEnergyTypeLockListener mEnergyTypeLockListener;

    private NumberType mNumberType = NumberType.AUTO_DETECT;

    /**
     * 是否锁定为新能源车牌类型
     */
    private boolean mIsNewEnergyTypeLocked = false;

    private final OnClickListener mOnEachInputCellClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final Button current = (Button) v;
            int currentViewIndex = 0;
            int currentSelectedIndex = -1;
            for (int i = 0; i < mSplitItems.length; i++) {
                final Button cell = mSplitItems[i];
                if (cell.isSelected()) {
                    currentSelectedIndex = i;
                }
                if (cell == current) {
                    currentViewIndex = i;
                }
                clearSelectedState(cell);
                // 不可跨位输入：如果当前点击的View的前面一个TextView是空的，则不可点击，保持原样。
                final int preIndex = currentViewIndex - 1;
                if (preIndex >= 0 &&
                        (mSplitItems[preIndex].getText().length() <= 0) &&
                        currentSelectedIndex >= 0) {
                    setSelectedState(mSplitItems[currentSelectedIndex]);
                    return;
                }
            }
            setSelectedState(current);
            for (OnItemSelectedListener listener : mOnItemSelectedListeners) {
                listener.onSelected(currentViewIndex);
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

        for (int i = 0; i < mSplitItems.length; i++) {
            mSplitItems[i] = (Button) findViewById(resIds[i]);
            mSplitItems[i].setOnClickListener(mOnEachInputCellClickListener);
            if (textSize > 0) {
                mSplitItems[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

        }
        // 最后一位默认隐藏
        set8thCellShowClearText(false, true);

    }

    public InputView setOnNewEnergyTypeLockListener(OnNewEnergyTypeLockListener listener) {
        mEnergyTypeLockListener = listener;
        return this;
    }

    public InputView addOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListeners.add(listener);
        return this;
    }

    public InputView addOnNumberTypeChangedListener(OnNumberTypeChangedListener listener) {
        mOnNumberTypeChangedListeners.add(listener);
        return this;
    }

    public InputView setOnShowMessageListener(OnShowMessageListener listener) {
        mOnShowMessageListener = listener;
        return this;
    }

    ////

    /**
     * 返回当前是否锁定在新能源车牌类型状态
     * @return 是否锁定
     */
    public boolean isNewEnergyTypeLocked() {
        return mIsNewEnergyTypeLocked;
    }

    /**
     * 返回当前的车牌号码是否为新能源车牌号码类型
     *
     * @return 是否为新能源类型
     */
    public boolean isNumberTypeOfNewEnergy() {
        return NumberType.NEW_ENERGY.equals(mNumberType);
    }

    /**
     * 设置是否锁定新能源车牌类型
     *
     * @param toLocked 是否锁定
     */
    public void setNewEnergyTypeLocked(boolean toLocked) {
        final boolean isCompleted = isCompleted();
        if (toLocked){
            if (checkIfCanChangeToNewEnergyType()) {
                mIsNewEnergyTypeLocked = true;
                triggerUpdateOfLockedState(isCompleted);
            }
        } else {
            mIsNewEnergyTypeLocked = false;
            triggerUpdateOfUnlocked(isCompleted);
        }
    }

    /**
     * 切换新能源车牌类型锁定状态
     */
    public void toggleNewEnergyTypeLockState() {
        setNewEnergyTypeLocked(!isNewEnergyTypeLocked());
    }

    /**
     * 设置文本字符到当前选中的输入框
     *
     * @param text 文本字符
     */
    public void setTextAndNext(final String text) {
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
                        selectNextItemBy(button);
                    }
                });
    }

    /**
     * 从最后一位开始删除
     */
    public void popNumberChar() {
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
                .allMatch(emptyFilter());
    }

    /**
     * 返回当前车牌号码是否被修改过。
     * 与通过 resetNumber 方法设置的车牌号码来对比。
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
    public void resetNumber(String number) {
        // 更新车牌号码，解除锁定新能源
        mIsNewEnergyTypeLocked = false;
        // 初始化车牌
        mKeyMap.put(KEY_INIT_NUMBER, number);
        final char[] chars = number.toCharArray();
        for (int i = 0; i < mSplitItems.length; i++) {
            final Button view = mSplitItems[i];
            if (i < chars.length) {
                view.setText(String.valueOf(chars[i]));
            } else {
                view.setText(null);
            }
        }
        // check 8th
        final boolean charsOf8 = chars.length >= 8;
        set8thCellShowClearText(charsOf8, !charsOf8);
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
     * 返回当前选中输入框的序号
     *
     * @return 选中序号
     */
    public int getCurrentSelectedIndex() {
        int index = 0;
        for (int i = 0; i < mSplitItems.length; i++) {
            if (mSplitItems[i].isShown() && mSplitItems[i].isSelected()) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 获取当前车牌号码的类型
     *
     * @return 号码类型
     */
    public NumberType getNumberType() {
        return mNumberType;
    }

    /**
     * 更新探测到的车牌类型。用于同步外部修改车牌号码后的车牌类型和输入框长度。
     */
    public void updateNumberType(NumberType type) {
        if (NumberType.NEW_ENERGY.equals(type) || mIsNewEnergyTypeLocked) {
            mIsNewEnergyTypeLocked = true;
        }
        // 根据车牌类型显示输入框长度：
        // 1. 新能源、武警地方车牌，长度为8位；
        // 2. 锁定为新能源状态下，固定为8位；
        if (NumberType.NEW_ENERGY.equals(type) ||
                NumberType.WUJING_LOCAL.equals(type) ||
                mIsNewEnergyTypeLocked) {
            set8thCellShowClearText(true, false);
        } else {
            // 在车辆不完整的情况下，最后一位显示时，要删除
            set8thCellShowClearText(false, !isCompleted());
        }
        changeNumberType(type);
        // 锁定状态提示
        if (mEnergyTypeLockListener != null) {
            mEnergyTypeLockListener.onTypeLock(mIsNewEnergyTypeLocked);
        }
    }

    /**
     * 选中第一个输入框
     */
    public void selectFirstItem() {
        performSelected(mSplitItems[0]);
    }

    /**
     * 选中最后一个可非空输入框。
     */
    public void selectLastItem() {
        findLastFilledTextItem()
                .ifPresent(new Consumer<Button>() {
                    @Override
                    public void accept(Button button) {
                        performSelected(button);
                    }
                });
    }

    ////


    // 当前已锁定为新能源车牌，可以转换成普通车牌
    private void triggerUpdateOfUnlocked(boolean isCompleted) {
        final int selectedIndex = getCurrentSelectedIndex();
        mOnShowMessageListener.onMessageTip(R.string.pwk_now_is_normal);
        updateNumberType(NumberType.AUTO_DETECT);
        try {
            for (OnNumberTypeChangedListener listener : mOnNumberTypeChangedListeners) {
                listener.onChanged(mNumberType);
            }
        } finally {
            // 从新能源车牌切换为普通车牌时：
            // 如果当前选中最后一位，则往前跳一位。
            if (isCompleted || selectedIndex == 7) {
                performSelected(mSplitItems[6]);
            }
        }
    }

    // 在普通车牌状态下，锁定为新能源车牌，需要检查是否符合新能源车牌规则
    private void triggerUpdateOfLockedState(boolean isCompleted) {
        updateNumberType(NumberType.NEW_ENERGY);
        mOnShowMessageListener.onMessageTip(R.string.pwk_now_is_energy);
        try {
            for (OnNumberTypeChangedListener listener : mOnNumberTypeChangedListeners) {
                listener.onChanged(mNumberType);
            }
        } finally {
            if (isCompleted) {
                selectNextItemBy(getCurrentSelectedItem());
            }
        }
    }

    private void performSelected(Button button) {
        button.performClick();
        setSelectedState(button);
    }

    private Optional<Button> findLastFilledTextItem() {
        final List<Button> reverse = new ArrayList<>(Arrays.asList(mSplitItems));
        Collections.reverse(reverse);
        return Stream.of(reverse)
                .filter(shownFilter())
                .filter(emptyFilter())
                .findFirst();
    }

    private boolean checkIfCanChangeToNewEnergyType() {
        // 车牌类型切换在“新能源普通”和“普通车牌”两种。
        // 1. 当前为新能源车牌，可以切换；
        // 2. 当前是其它普通车牌，检查当前是否符合新能源车牌规则；
        if (NumberType.NEW_ENERGY.equals(mNumberType)) {
            return true;
        } else {
            String number = getNumber();
            if (number.length() > 2) {
                final int size = 8 - number.length();
                for (int i = 0; i < size; i++) number += "0";
                if (Pattern.matches("\\w[A-Z][0-9DF][0-9A-Z]\\d{3}[0-9DF]", number)) {
                    return true;
                } else {
                    mOnShowMessageListener.onMessageError(R.string.pwk_change_to_energy_disallow);
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    private void set8thCellShowClearText(boolean show, boolean clearText) {
        mSplitItems[7].setVisibility(show ? VISIBLE : GONE);
        if (clearText) {
            mSplitItems[7].setText(null);
        }
    }

    private void changeNumberType(NumberType willType) {
        if (mIsNewEnergyTypeLocked) {
            mNumberType = NumberType.NEW_ENERGY;
        } else {
            mNumberType = willType;
        }
    }

    private Button getCurrentSelectedItem() {
        return mSplitItems[getCurrentSelectedIndex()];
    }

    private void selectNextItemBy(Button cell) {
        final List<Button> buttons = streamOfShownItems()
                .collect(Collectors.<Button>toList());
        final int nextIndex = buttons.lastIndexOf(cell) + 1;
        final int clickIndex = Math.min(nextIndex, buttons.size() - 1);
        buttons.get(clickIndex).performClick();
    }

    private void clearSelectedState(Button item) {
        item.setSelected(false);
    }

    private void setSelectedState(Button cell) {
        for (Button btn : mSplitItems) {
            btn.setSelected((btn == cell));
        }
    }

    private Stream<Button> streamOfShownItems() {
        return Stream.of(mSplitItems)
                .filter(shownFilter());
    }

    private Predicate<Button> shownFilter() {
        return new Predicate<Button>() {
            @Override
            public boolean test(Button value) {
                return value.isShown();
            }
        };
    }

    private Predicate<Button> emptyFilter() {
        return new Predicate<Button>() {
            @Override
            public boolean test(Button value) {
                return value.getText().length() > 0;
            }
        };
    }

    //////////

    public interface OnItemSelectedListener {

        void onSelected(int index);
    }

    public interface OnNumberTypeChangedListener {

        void onChanged(NumberType type);
    }

    public interface OnShowMessageListener {

        void onMessageError(int message);

        void onMessageTip(int message);
    }

    public interface OnNewEnergyTypeLockListener {

        void onTypeLock(boolean locked);
    }

}
