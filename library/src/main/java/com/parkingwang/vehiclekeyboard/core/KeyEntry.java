/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.vehiclekeyboard.core;

/**
 * 按键
 *
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @since 2017-09-25 0.1
 */
public class KeyEntry {
    /**
     * 当前键位显示的文本
     */
    public final String text;
    /**
     * 当前键位按键类型码
     */
    public final KeyType keyType;
    /**
     * 当前键位在键盘布局中是否可用
     */
    public final boolean enabled;
    /**
     * 当前键位是否为功能性的按键
     */
    public final boolean isFunKey;

    public KeyEntry(String text, int keyCode, boolean enabled, boolean isFunKey) {
        this.text = text;
        this.keyType = KeyType.values()[keyCode];
        this.enabled = enabled;
        this.isFunKey = isFunKey;
    }

    @Override
    public String toString() {
        return "KeyEntry{" +
                "text='" + text + '\'' +
                ", keyCode=" + keyType +
                ", enabled=" + enabled +
                ", isFunKey=" + isFunKey +
                '}';
    }
}
