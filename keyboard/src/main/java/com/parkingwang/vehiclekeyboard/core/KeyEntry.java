package com.parkingwang.vehiclekeyboard.core;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public class KeyEntry {
    public final String text;
    public final KeyType keyType;
    public final boolean isFunKey;
    public final boolean enabled;

    public KeyEntry(String text, KeyType keyType, boolean enabled, boolean isFunKey) {
        this.text = text;
        this.keyType = keyType;
        this.enabled = enabled;
        this.isFunKey = isFunKey;
    }

    @Override
    public String toString() {
        return "KeyEntry{" +
                "text='" + text + '\'' +
                ", keyType=" + keyType +
                ", isFunKey=" + isFunKey +
                ", enabled=" + enabled +
                '}';
    }
}
