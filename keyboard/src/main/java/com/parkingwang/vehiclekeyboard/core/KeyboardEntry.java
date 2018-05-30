package com.parkingwang.vehiclekeyboard.core;

import java.util.List;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class KeyboardEntry {
    /**
     * 当前光标所处的位置
     */
    public final int index;
    /**
     * 当前预设的车牌号码
     */
    public final String presetNumber;
    /**
     * 当前预设的车牌号码类型
     */
    public final NumberType presetNumberType;
    /**
     * 当前预设的车牌号码长度
     */
    public final int numberLength;
    /**
     * 当前车牌号码的最大长度
     */
    public final int numberMaxLength;
    /**
     * 键盘里的所有键位
     */
    public final List<List<KeyEntry>> keyRows;

    /**
     * 当前车牌号码的检测类型
     */
    public final NumberType detectedNumberType;

    public KeyboardEntry(int index, String presetNumber, NumberType presetNumberType,
                         int numberLength, int numberMaxLength, List<List<KeyEntry>> keyRows, NumberType detectedNumberType) {
        this.index = index;
        this.presetNumber = presetNumber;
        this.presetNumberType = presetNumberType;
        this.numberLength = numberLength;
        this.numberMaxLength = numberMaxLength;
        this.keyRows = keyRows;
        this.detectedNumberType = detectedNumberType;
    }

    @Override
    public String toString() {
        return "KeyboardEntry{" +
                "index=" + index +
                ", presetNumber='" + presetNumber + '\'' +
                ", presetNumberType=" + presetNumberType +
                ", numberLength=" + numberLength +
                ", numberMaxLength=" + numberMaxLength +
                ", keyRows=" + keyRows +
                ", detectedNumberType=" + detectedNumberType +
                '}';
    }
}
