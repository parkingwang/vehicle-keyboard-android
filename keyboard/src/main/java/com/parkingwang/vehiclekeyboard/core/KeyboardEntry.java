package com.parkingwang.vehiclekeyboard.core;

import java.util.List;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class KeyboardEntry {
    /**
     * 当前输入车牌号码的位置
     */
    public final int selectedPosition;
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
    public final List<List<KeyEntry>> keyRows;

    /**
     * 当前输入的车牌号码所属的车牌类型
     */
    public final NumberType currentNumberType;

    public KeyboardEntry(int selectedPosition, String presetNumber, int numberMaxLength, List<List<KeyEntry>> keyRows, NumberType currentNumberType) {
        this.selectedPosition = selectedPosition;
        this.presetNumber = presetNumber;
        this.numberMaxLength = numberMaxLength;
        this.keyRows = keyRows;
        this.currentNumberType = currentNumberType;
    }

    @Override
    public String toString() {
        return "KeyboardEntry{" +
                "index=" + selectedPosition +
                ", presetNumber='" + presetNumber + '\'' +
                ", numberMaxLength=" + numberMaxLength +
                ", keyRows=" + keyRows +
                ", detectedNumberType=" + currentNumberType +
                '}';
    }
}
