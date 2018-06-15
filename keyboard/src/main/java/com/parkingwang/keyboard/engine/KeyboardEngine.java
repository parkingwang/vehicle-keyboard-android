package com.parkingwang.keyboard.engine;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class KeyboardEngine {

    private final AvailableKeyRegistry mKeyRegistry = new AvailableKeyRegistry();
    private final LayoutManager mKeyboardLayout = new LayoutManager();
    private final Mixer mMixer = new Mixer();

    public KeyboardEngine() {
        mMixer.addMapper(new FuncKeyTransformer());
        mMixer.addMapper(new MoreKeyTransformer());
        mMixer.addMapper(new BackKeyTransformer());
    }

    /**
     * @param hide 设置是否不显示“确定”键
     */
    public void setHideOKKey(boolean hide) {
        mMixer.setRemoveFuncOK(hide);
    }

    /**
     * 更新键盘布局
     *
     * @param presetNumber    预设车牌号码
     * @param selectCharIndex 当前选中的车牌字符序号
     * @param showMoreLayout  是否显示对应序号的“更多”状态的键盘布局
     * @param fixedNumberType 指定车牌号码类型。要求引擎内部只按此类型来处理。
     * @return 键盘布局
     */
    public KeyboardEntry update(String presetNumber, int selectCharIndex, boolean showMoreLayout, NumberType fixedNumberType) {
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
                mKeyRegistry.available(detectNumberType, selectCharIndex),
                showMoreLayout);

        final LayoutEntry layout = mKeyboardLayout.getLayout(context);
        final LayoutEntry output = mMixer.mix(context, layout);

        return new KeyboardEntry(selectCharIndex, presetNumber, maxLength, output, detectNumberType);
    }

}
