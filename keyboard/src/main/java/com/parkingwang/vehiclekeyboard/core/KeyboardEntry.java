/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.vehiclekeyboard.core;

import java.util.List;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2017-9-26 0.1
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
     * 当前键盘所处的键盘类型
     */
    public final KeyboardType keyboardType;
    /**
     * 当前预设的车牌号码类型
     * @since 0.3
     */
    public final NumberType presetNumberType;
    /**
     * 当前预设的车牌号码长度
     */
    public final int numberLength;
    /**
     * 当前车牌号码的最大长度
     */
    public final int numberLimitLength;
    /**
     * 键盘里的所有键位
     */
    public final List<List<KeyEntry>> keyRows;
    /**
     * 当前车牌号码的检测类型
     * @since 0.3
     */
    public final NumberType detectedNumberType;

    /**
     * @since 0.3
     */
    public KeyboardEntry(int index, String presetNumber, KeyboardType keyboardType,
                         NumberType presetNumberType, int numberLength, int numberLimitLength,
                         List<List<KeyEntry>> keyRows, NumberType detectedNumberType) {
        this.index = index;
        this.presetNumber = presetNumber;
        this.keyboardType = keyboardType;
        this.presetNumberType = presetNumberType;
        this.numberLength = numberLength;
        this.numberLimitLength = numberLimitLength;
        this.keyRows = keyRows;
        this.detectedNumberType = detectedNumberType;
    }

    @Override
    public String toString() {
        return "KeyboardEntry{" +
                "index=" + index +
                ", presetNumber='" + presetNumber + '\'' +
                ", keyboardType=" + keyboardType +
                ", presetNumberType=" + presetNumberType +
                ", numberLength=" + numberLength +
                ", numberLimitLength=" + numberLimitLength +
                ", keyRows=" + keyRows +
                ", detectedNumberType=" + detectedNumberType +
                '}';
    }
}
