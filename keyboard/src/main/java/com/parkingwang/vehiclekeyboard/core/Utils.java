package com.parkingwang.vehiclekeyboard.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.parkingwang.vehiclekeyboard.core.VNumberChars.BACK;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.DEL;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.MORE;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.OK;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
class Utils {
    private Utils() {

    }

    static <T> List<T> append(List<T>... items) {
        final List<T> output = new ArrayList<T>();
        for (List<T> s : items) {
            output.addAll(s);
        }
        return output;
    }

    static List<KeyEntry> mkEntitiesOf(String keysStr) {
        final List<KeyEntry> keys = new ArrayList<>();
        for (int i = 0; i < keysStr.length(); i++) {
            String keyChar = String.valueOf(keysStr.charAt(i));
            keys.add(mkEntry(keyChar));
        }
        return keys;
    }

    private static KeyEntry mkEntry(String text) {
        final KeyType keyType;
        switch (text) {
            case DEL:
                keyType = KeyType.FUNC_DELETE;
                break;
            case OK:
                keyType = KeyType.FUNC_OK;
                break;
            case MORE:
                keyType = KeyType.FUNC_MORE;
                break;
            case BACK:
                keyType = KeyType.FUNC_BACK;
                break;
            default:
                keyType = KeyType.GENERAL;
        }
        return new KeyEntry(text, keyType, false, !keyType.equals(KeyType.GENERAL));
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }
}
