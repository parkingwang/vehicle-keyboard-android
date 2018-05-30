package com.parkingwang.vehiclekeyboard.core;

import java.util.List;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class Env {
    final String presetNumber;
    final int selectIndex;
    final NumberType numberType;
    final int limitLength;
    final List<KeyEntry> availableKeys;

    public Env(String presetNumber, int selectIndex, NumberType numberType,
               int limitLength, List<KeyEntry> availableKeys) {
        this.presetNumber = presetNumber;
        this.selectIndex = selectIndex;
        this.numberType = numberType;
        this.limitLength = limitLength;
        this.availableKeys = availableKeys;
    }
}