package com.parkingwang.vehiclekeyboard.core;

import static com.parkingwang.vehiclekeyboard.core.VNumberChars.CHARS_PLA2012;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.NUMERIC_123;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
public enum NumberType {
    /**
     * 未知类型
     */
    AUTO_DETECT,

    /**
     * 民用
     */
    CIVIL,

    /**
     * 新能源车牌
     */
    NEW_ENERGY,

    /**
     * 港澳
     */
    HK_MACAO,

    /**
     * 新式武警车牌
     */
    WJ2012,

    /**
     * 新式军车车牌
     */
    PLA2012,

    /**
     * 旧式大使馆车牌
     */
    SHI2012,

    /**
     * 新式大使馆车牌
     */
    SHI2017,

    /**
     * 旧式领事馆车牌
     */
    LING2012,

    /**
     * 新式领事馆车牌
     */
    LING2018,

    /**
     * 民航车牌
     */
    AVIATION;

    /**
     * 检测车牌号码所属的车牌号码类型
     *
     * @param number 车牌号码
     * @return 号码类型
     */
    public static NumberType detect(String number) {
        if (null == number) {
            return NumberType.AUTO_DETECT;
        }
        final int size = number.length();
        if (0 == size) {
            return NumberType.AUTO_DETECT;
        }
        number = number.toUpperCase();
        final char firstChar = number.charAt(0);
        // 军队
        if (contains(CHARS_PLA2012, firstChar)) {
            return PLA2012;
        }
        // 使147001
        if (VNumberChars.SHI == firstChar) {
            return SHI2012;
        }
        // 146001使
        if (contains(NUMERIC_123, firstChar)) {
            return SHI2017;
        }
        // 民航
        if (VNumberChars.MIN == firstChar) {
            return AVIATION;
        }
        // 武警
        if (VNumberChars.WJ_W == firstChar) {
            return WJ2012;
        }
        final char lastChar = number.charAt(Math.max(0, size - 1));
        // 领事馆
        if (VNumberChars.LING == lastChar) {
            // 粤17601领
            // 粤A0011领
            if (size > 2) {
                final char secondChar = number.charAt(1);
                if (contains(VNumberChars.QWERTY_has_O, secondChar)) {
                    return LING2012;
                } else {
                    return LING2018;
                }
            } else {
                return LING2018;
            }
        }
        // 港澳
        if (number.startsWith("粤Z") || number.contains(VNumberChars.CHARS_HK_MACAO)) {
            return HK_MACAO;
        }
        if (size == 8) {
            return NEW_ENERGY;
        } else {
            return CIVIL;
        }
    }

    public int maxLength() {
        switch (this) {
            case WJ2012:
            case NEW_ENERGY:
                return 8;

            default:
                return 7;
        }
    }

    private static boolean contains(String s, char c) {
        return s.indexOf(c) >= 0;
    }
}


