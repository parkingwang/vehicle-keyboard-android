package com.parkingwang.keyboard.engine;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class KeyboardEntry {
    /**
     * 当前输入车牌号码的位置
     */
    public final int selectIndex;
    /**
     * 当前预设的车牌号码
     */
    public final String presetNumber;
    /**
     * 当前车牌号码的最大长度
     */
    public final int numberMaxLength;
    /**
     * 键盘里的所有键位
     */
    public final LayoutEntry layout;

    /**
     * 当前输入的车牌号码所属的车牌类型
     */
    public final NumberType currentNumberType;

    public KeyboardEntry(int selectIndex, String presetNumber, int numberMaxLength, LayoutEntry layout, NumberType currentNumberType) {
        this.selectIndex = selectIndex;
        this.presetNumber = presetNumber;
        this.numberMaxLength = numberMaxLength;
        this.layout = layout;
        this.currentNumberType = currentNumberType;
    }

    @Override
    public String toString() {
        return "KeyboardEntry{" +
                "index=" + selectIndex +
                ", presetNumber='" + presetNumber + '\'' +
                ", numberMaxLength=" + numberMaxLength +
                ", layout=" + layout +
                ", detectedNumberType=" + currentNumberType +
                '}';
    }
}
