package com.parkingwang.vehiclekeyboard.core;

import android.util.Log;

import java.util.List;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class Engine {

    private static final String TAG = "KeyboardEngine";

    private final KeysRegistry mKeysRegistry = new KeysRegistry();
    private final LayoutRegistry mLayoutRegistry = new LayoutRegistry();
    private final Mixer mMixer = new Mixer();

    private KeyboardType mKeyboardType = KeyboardType.CIVIL_ONLY;

    public Engine() {
        mMixer.addFilter(new FuncKeyFilter());
        mMixer.addFilter(new HKMacaoKeyFilter());
    }

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
        presetNumber = null == presetNumber ? "" : presetNumber.toUpperCase();
        selectIndex = Math.max(0, Math.min(selectIndex, 8));

        Log.d(TAG, String.format("Update:: presetNumber= %s, selectIndex= %d", presetNumber, selectIndex));

        final NumberType presetType = NumberType.detect(presetNumber);
        final NumberType detectType;
        if (NumberType.AUTO_DETECT.equals(userType)) {
            detectType = presetType;
        } else {
            detectType = userType;
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

        Log.d(TAG, "Detected:: numberType= " + detectType.name());

        // 混合成可以输出使用的键位
        final List<KeyEntry> keys = mKeysRegistry.getAvailable(detectType, selectIndex);
        final List<List<KeyEntry>> layout = mLayoutRegistry.getAvailable(mKeyboardType, selectIndex);
        final List<List<KeyEntry>> output = mMixer.mix(new Mixer.Env(presetNumber, selectIndex, detectType, limitLength, keys, layout));


        return new KeyboardEntry(selectIndex, presetNumber, mKeyboardType,
                presetType, presetNumber.length(), limitLength, output, detectType);
    }

}
