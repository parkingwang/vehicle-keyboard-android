package com.parkingwang.vehiclekeyboard.core;

import android.util.Log;

import java.util.List;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class Engine {

    public static final int INDEX_PREFIX = SpecIndex.MORE_PREFIX;
    public static final int INDEX_POSTFIX = SpecIndex.MORE_POSTFIX;

    private static final String TAG = "KeyboardEngine";

    private final PrepareKeyRegistry mPrepareKeyRegistry = new PrepareKeyRegistry();
    private final LayoutRegistry mLayoutRegistry = new LayoutRegistry();
    private final Mixer mMixer = new Mixer();

    public Engine() {
        mMixer.addMapper(new GeneralKeyMapper());
    }

    /**
     * 更新键盘布局
     *
     * @param presetNumber    预设车牌号码
     * @param selectIndex     当前选中的车牌序号
     * @param fixedNumberType 指定车牌号码类型。要求引擎内部只按此类型来处理。
     * @return 键盘布局
     */
    public KeyboardEntry update(String presetNumber, int selectIndex, NumberType fixedNumberType) {
        final NumberType presetNumberType = NumberType.detect(presetNumber);
        final NumberType detectNumberType;
        if (NumberType.AUTO_DETECT.equals(fixedNumberType)) {
            detectNumberType = presetNumberType;
        } else {
            detectNumberType = fixedNumberType;
        }

        // 车牌限制长度
        final int maxLength = detectNumberType.maxLength();

        // 混合成可以输出使用的键位
        final Env env = new Env(
                presetNumber,
                selectIndex,
                detectNumberType,
                maxLength,
                mPrepareKeyRegistry.available(detectNumberType, selectIndex));

        final List<List<KeyEntry>> layout = mLayoutRegistry.layout(env, selectIndex);
        final List<List<KeyEntry>> output = mMixer.mix(env, layout);

        Log.d(TAG, "当前车牌类型：" + detectNumberType.name());
        Log.d(TAG, "当前可用键位：" + env.availableKeys);

        return new KeyboardEntry(selectIndex, presetNumber,
                presetNumberType, presetNumber.length(), maxLength, output, detectNumberType);
    }

}
