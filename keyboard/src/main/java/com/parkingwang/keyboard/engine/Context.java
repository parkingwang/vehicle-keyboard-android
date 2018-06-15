package com.parkingwang.keyboard.engine;

import com.parkingwang.keyboard.neighbor.Province;

import java.util.List;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
class Context {

    final String presetNumber;
    final int selectIndex;
    final NumberType numberType;
    final int limitLength;
    final List<KeyEntry> availableKeys;
    final boolean reqSpecLayout;
    final Province province;

    public Context(String presetNumber, int selectIndex, NumberType numberType,
                   int limitLength, List<KeyEntry> availableKeys, boolean reqSpecLayout, Province province) {
        this.presetNumber = presetNumber;
        this.selectIndex = selectIndex;
        this.numberType = numberType;
        this.limitLength = limitLength;
        this.availableKeys = availableKeys;
        this.reqSpecLayout = reqSpecLayout;
        this.province = province;
    }
}