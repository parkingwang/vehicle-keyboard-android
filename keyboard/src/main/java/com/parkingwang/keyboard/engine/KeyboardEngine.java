package com.parkingwang.keyboard.engine;

import com.parkingwang.keyboard.neighbor.NeighborManager;
import com.parkingwang.keyboard.neighbor.Province;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class KeyboardEngine {

    private final AvailableKeyRegistry mKeyRegistry = new AvailableKeyRegistry();
    private final LayoutManager mKeyboardLayout = new LayoutManager();
    private final NeighborManager mNeighborManager = new NeighborManager();
    private final LayoutMixer mLayoutMixer = new LayoutMixer();

    private final RemoveOKLayoutTransformer mRemoveOKLayoutTransformer = new RemoveOKLayoutTransformer();
    private String mLocationProvince;

    public KeyboardEngine() {
        // keys
        mLayoutMixer.addKeyTransformer(new FuncKeyTransformer());
        mLayoutMixer.addKeyTransformer(new MoreKeyTransformer());
        mLayoutMixer.addKeyTransformer(new BackKeyTransformer());
        // layout
        mLayoutMixer.addLayoutTransformer(mRemoveOKLayoutTransformer);
        mLayoutMixer.addLayoutTransformer(new NeighborLayoutTransformer());
    }

    /**
     * @param hide 设置是否不显示“确定”键
     */
    public void setHideOKKey(boolean hide) {
        mRemoveOKLayoutTransformer.setRemoveEnabled(hide);
    }

    /**
     * 设置当前位置省份名称。如果设置此名称，会重新调整省份布局，将当前省份周边的几个省份，排列在第一行前几位。
     *
     * @param provinceName 省份名称
     */
    public void setLocalProvinceName(String provinceName) {
        mLocationProvince = provinceName;
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

        // 根据预设省份，重排键盘布局
        final Province province = mNeighborManager.getLocation(mLocationProvince);

        // 混合成可以输出使用的键位
        final Context context = new Context(
                presetNumber,
                selectCharIndex,
                detectNumberType,
                maxLength,
                mKeyRegistry.available(detectNumberType, selectCharIndex),
                showMoreLayout,
                province);

        final LayoutEntry layout = mKeyboardLayout.getLayout(context);
        final LayoutEntry tr = mLayoutMixer.transform(context, layout);
        final LayoutEntry output = mLayoutMixer.mix(context, tr);

        return new KeyboardEntry(selectCharIndex, presetNumber, maxLength, output, detectNumberType);
    }

}
