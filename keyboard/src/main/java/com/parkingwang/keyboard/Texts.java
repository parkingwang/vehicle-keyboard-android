package com.parkingwang.keyboard;

import java.util.regex.Pattern;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com), 陈哈哈
 * @version 0.3-ALPHA
 * @since 0.1
 */
public class Texts {
    private static Pattern ENGLISH_LETTER_DIGITS = Pattern.compile("[^a-zA-Z0-9]");

    public static boolean isEnglishLetterOrDigit(String str) {
        return !ENGLISH_LETTER_DIGITS.matcher(str).find();
    }

    public static boolean isNewEnergyType(String number) {
        if (number != null && number.length() > 2) {
            final int size = 8 - number.length();
            for (int i = 0; i < size; i++) {
                number += "0";
            }
            if (Pattern.matches("\\w[A-Z][0-9A-Z][0-9A-Z]\\w{3}[0-9A-Z]", number)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
