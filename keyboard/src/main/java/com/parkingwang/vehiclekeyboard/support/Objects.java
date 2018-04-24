package com.parkingwang.vehiclekeyboard.support;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class Objects {

    private Objects() {
    }

    public static <T> T notNull(T val) {
        if (null == val) {
            throw new NullPointerException("Null pointer");
        }
        return val;
    }
}
