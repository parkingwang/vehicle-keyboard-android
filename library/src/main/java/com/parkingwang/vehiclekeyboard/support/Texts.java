package com.parkingwang.vehiclekeyboard.support;

import android.support.v4.util.PatternsCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * @author
 * @version 2017-11-02
 * @since 2017-11-02
 */
public class Texts {
    private static final Pattern LETTER_OR_DIGIT = Pattern.compile("[^a-zA-Z0-9]");
    public static boolean isLetterOrDigitOnly(String str) {
        return !LETTER_OR_DIGIT.matcher(str).find();
    }
}
