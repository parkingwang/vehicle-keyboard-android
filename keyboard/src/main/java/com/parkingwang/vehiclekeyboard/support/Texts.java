package com.parkingwang.vehiclekeyboard.support;

import java.util.regex.Pattern;

public class Texts {

    public static boolean isLetterOrDigitOnly(String str) {
        for (char c : str.toCharArray()) {
            if (! Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    static boolean isNewEnergyType(String number) {
        if (number != null && number.length() > 2) {
            final int size = 8 - number.length();
            for (int i = 0; i < size; i++) number += "0";
            if (Pattern.matches("\\w[A-Z][0-9DF][0-9A-Z]\\d{3}[0-9DF]", number)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
