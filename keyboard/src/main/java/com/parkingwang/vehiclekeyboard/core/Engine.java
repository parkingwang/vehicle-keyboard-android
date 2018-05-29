package com.parkingwang.vehiclekeyboard.core;

import java.util.ArrayList;
import java.util.List;

import static com.parkingwang.vehiclekeyboard.core.VNumberChars.DEL;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.MORE;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.OK;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class Engine {

    private final KeysRegistry mKeysRegistry = new KeysRegistry();
    private final LayoutRegistry mLayoutRegistry = new LayoutRegistry();

    private KeyboardType mKeyboardType = KeyboardType.CIVIL_ONLY;

    public void SetKeyboardType(KeyboardType keyboardType) {
        mKeyboardType = keyboardType;
    }

    /**
     * 更新键盘布局
     *
     * @param presetNumber 预设车牌号码
     * @param selectIndex  当前选中的车牌序号
     * @param userType     指定车牌号码类型
     * @return 键盘布局
     */
    public KeyboardEntry update(String presetNumber, int selectIndex, NumberType userType) {
        // 修正参数
        presetNumber = null == presetNumber ? "" : presetNumber;
        selectIndex = Math.max(0, Math.min(selectIndex, 8));

        final NumberType presetType = NumberType.detect(presetNumber);
        final NumberType detectType;
        if (NumberType.AUTO_DETECT.equals(userType)) {
            detectType = presetType;
        } else {
            detectType = userType;
        }
        // 当前车牌和选中位置，可用的键位数据
        final List<KeyEntry> availableKeys = mKeysRegistry.getAvailable(detectType, selectIndex);

        // 获取当前键盘类型的布局
        final List<List<KeyEntry>> layout = mLayoutRegistry.getAvailable(mKeyboardType, selectIndex);

        // 混合成可以输出使用的键位
        final List<List<KeyEntry>> output = new ArrayList<>(layout.size());
        for (List<KeyEntry> rows : layout) {
            // 检查Row中的键位，是否在available中。
            final List<KeyEntry> newRow = new ArrayList<>(rows.size());
            // 存在则更新Row的键位状态
            for (KeyEntry key : rows) {
                // 过滤特殊字符
                final String text;
                switch (key.text) {
                    case OK:
                        text = "确定";
                        break;

                    case DEL:
                        text = "删除";
                        break;

                    case MORE:
                        text = "更多";
                        break;

                    default:
                        text = key.text;

                }
                final boolean enabled = isAvailable(availableKeys, key);
                newRow.add(new KeyEntry(text, key.keyType, enabled, key.isFunKey));
            }
            output.add(newRow);
        }

        // 车牌限制长度
        final int limitLength;
        switch (detectType) {
            case WJ2012:
            case NEW_ENERGY:
                limitLength = 8;
                break;

            default:
                limitLength = 7;
        }

        return new KeyboardEntry(selectIndex, presetNumber, mKeyboardType,
                presetType, presetNumber.length(), limitLength, output, detectType);
    }

    private static boolean isAvailable(List<KeyEntry> availableKeys, KeyEntry key) {
        for (KeyEntry ak : availableKeys) {
            if (ak.text.equals(key.text)) {
                return true;
            }
        }
        return false;
    }
}
