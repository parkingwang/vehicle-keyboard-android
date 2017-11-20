package com.parkingwang.vehiclekeyboard.support;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author 黄浩杭
 * @version 2017-11-19 0.2.3
 * @since 2017-11-19 0.2.3
 */
public class TextsTest {
    @Test
    public void testIsEnglishLetterOrDigitsOnly() {
        Assert.assertTrue(Texts.isEnglishLetterOrDigit("A"));
        Assert.assertTrue(Texts.isEnglishLetterOrDigit("1"));
        Assert.assertFalse(Texts.isEnglishLetterOrDigit("粤"));
        Assert.assertFalse(Texts.isEnglishLetterOrDigit("确定"));
    }
}
