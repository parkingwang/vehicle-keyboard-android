package com.parkingwang.keyboard.engine;

import java.util.List;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class KeyboardEngine {

    public static final int INDEX_PREFIX = SpecIndex.MORE_PREFIX;
    public static final int INDEX_POSTFIX = SpecIndex.MORE_POSTFIX;

    private static final String TAG = "KeyboardEngine";

    private final AvailableKeyRegistry mAvailableKeyRegistry = new AvailableKeyRegistry();
    private final LayoutManager mKeyboardLayout = new LayoutManager();
    private final Mixer mMixer = new Mixer();

    public KeyboardEngine() {
        mMixer.addMapper(new FuncKeyTransformer());
        mMixer.addMapper(new MoreKeyTransformer());
        mMixer.addMapper(new BackKeyTransformer());
    }

    /**
     * 更新键盘布局
     *
     * @param presetNumber    预设车牌号码
     * @param selectCharIndex 当前选中的车牌字符序号
     * @param fixedNumberType 指定车牌号码类型。要求引擎内部只按此类型来处理。
     * @return 键盘布局
     */
    public KeyboardEntry update(String presetNumber, int selectCharIndex, NumberType fixedNumberType) {
        final NumberType detectNumberType;
        if (NumberType.AUTO_DETECT.equals(fixedNumberType)) {
            detectNumberType = NumberType.detect(presetNumber);
        } else {
            detectNumberType = fixedNumberType;
        }

        // 车牌限制长度
        final int maxLength = detectNumberType.maxLength();

        // 混合成可以输出使用的键位
        final Context context = new Context(
                presetNumber,
                selectCharIndex,
                detectNumberType,
                maxLength,
                mAvailableKeyRegistry.available(detectNumberType, selectCharIndex));

        final List<List<KeyEntry>> layout = mKeyboardLayout.layout(context, selectCharIndex);
        final List<List<KeyEntry>> output = mMixer.mix(context, layout);

        return new KeyboardEntry(selectCharIndex, presetNumber, maxLength, output, detectNumberType);
    }

}
