package com.parkingwang.keyboard;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.parkingwang.keyboard.engine.KeyEntry;
import com.parkingwang.keyboard.engine.KeyboardEntry;
import com.parkingwang.keyboard.view.InputView;
import com.parkingwang.keyboard.view.OnKeyboardChangedListener;

import java.util.List;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
class AutoCommit extends OnKeyboardChangedListener.Simple {

    private final InputView mInputView;

    private boolean mIsDeleteAction = false;

    public AutoCommit(InputView inputView) {
        mInputView = inputView;
    }

    @Override
    public void onTextKey(String text) {
        mIsDeleteAction = false;
    }

    @Override
    public void onDeleteKey() {
        mIsDeleteAction = true;
    }

    @Override
    public void onKeyboardChanged(KeyboardEntry keyboard) {
        // 在第2位和第7位的字符输入位置，存在着可以自动提交的单个键位。
        // FIXME 第2位按键存在问题：执行performClick但未能自动跳转到下一位。
        if (6 != keyboard.selectedPosition) {
            return;
        }
        // 如果可点击键位只有一个，并且前一个操作不是删除键，则自动提交
        if (mIsDeleteAction) {
            return;
        }

        // 如果存在唯一文本键位，则自动提交
        final KeyEntry key = singleKey(keyboard);
        if (key != null) {
            mIsDeleteAction = false;
            mInputView.updateSelectedCharAndSelectNext(key.text);
        }
    }

    private KeyEntry singleKey(KeyboardEntry keyboard) {
        final List<KeyEntry> keys = Stream.of(keyboard.keyRows)
                .flatMap(new Function<List<KeyEntry>, Stream<KeyEntry>>() {
                    @Override
                    public Stream<KeyEntry> apply(List<KeyEntry> keyEntries) {
                        return Stream.of(keyEntries);
                    }
                })
                .filter(new Predicate<KeyEntry>() {
                    @Override
                    public boolean test(KeyEntry key) {
                        return !key.isFunKey && key.enabled;
                    }
                })
                .collect(Collectors.<KeyEntry>toList());

        if (1 == keys.size()) {
            return keys.get(0);
        } else {
            return null;
        }
    }
}
