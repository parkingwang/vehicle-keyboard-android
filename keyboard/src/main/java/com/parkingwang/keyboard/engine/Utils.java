package com.parkingwang.keyboard.engine;

import java.util.Arrays;

import static com.parkingwang.keyboard.engine.VNumberChars.BACK;
import static com.parkingwang.keyboard.engine.VNumberChars.DEL;
import static com.parkingwang.keyboard.engine.VNumberChars.MORE;
import static com.parkingwang.keyboard.engine.VNumberChars.OK;

/**
 * @author 陈哈哈 yoojiachen@gmail.com
 */
class Utils {
    private Utils() {

    }

    static RowEntry append(RowEntry... items) {
        final RowEntry output = new RowEntry();
        for (RowEntry s : items) {
            output.addAll(s);
        }
        return output;
    }

    static RowEntry mkEntitiesOf(String keysStr) {
        final RowEntry keys = new RowEntry();
        for (int i = 0; i < keysStr.length(); i++) {
            String keyChar = String.valueOf(keysStr.charAt(i));
            keys.add(mkEntry(keyChar));
        }
        return keys;
    }

    static KeyEntry mkEntry(String text) {
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
        return new KeyEntry(text, keyType, false);
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }
}
