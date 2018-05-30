package com.parkingwang.vehiclekeyboard.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.parkingwang.vehiclekeyboard.core.KeyboardType.CIVIL_ONLY;
import static com.parkingwang.vehiclekeyboard.core.KeyboardType.CIVIL_SPEC;
import static com.parkingwang.vehiclekeyboard.core.KeyboardType.FULL;
import static com.parkingwang.vehiclekeyboard.core.Utils.mkEntitiesOf;


/**
 * 车牌号码类型对应的布局管理
 *
 * @author 陈哈哈 yoojiachen@gmail.com
 */
class LayoutRegistry {

    private final Map<String, List<List<KeyEntry>>> mLayouts = new HashMap<>();

    LayoutRegistry() {
        //// 民用键盘
        mLayouts.put(mkKey(CIVIL_ONLY, 0), createRows(
                "京津晋冀蒙辽吉黑沪",
                "苏浙皖闽赣鲁豫鄂",
                "湘粤桂琼渝川贵云",
                "藏陕甘青宁新-+"
        ));
        mLayouts.put(mkKey(CIVIL_ONLY, 1), createRows(
                "1234567890",
                "QWERTYUOP",
                "ASDFGHJKL",
                "ZXCVBNM-+"
        ));
        final List<List<KeyEntry>> civil = createRows(
                "1234567890",
                "QWERTYUP港澳",
                "ASDFGHJKL学",
                "ZXCVBNM警-+"
        );
        mLayouts.put(mkKey(CIVIL_ONLY, 2), civil);
        mLayouts.put(mkKey(CIVIL_ONLY, 3), civil);
        mLayouts.put(mkKey(CIVIL_ONLY, 4), civil);
        mLayouts.put(mkKey(CIVIL_ONLY, 5), civil);
        mLayouts.put(mkKey(CIVIL_ONLY, 6), civil);
        mLayouts.put(mkKey(CIVIL_ONLY, 7), civil);

        //// 民用特殊键盘
        mLayouts.put(mkKey(CIVIL_SPEC, 0), createRows(
                "京津晋冀蒙辽吉黑沪",
                "苏浙皖闽赣鲁豫鄂湘",
                "粤桂琼渝川贵云藏陕",
                "甘青宁新W-+"
        ));
        mLayouts.put(mkKey(CIVIL_SPEC, 1), createRows(
                "1234567890",
                "QWERTYUOP",
                "ASDFGHJKL",
                "ZXCVBNM-+"
        ));
        final List<List<KeyEntry>> spec = createRows(
                "1234567890",
                "QWERTYUP港澳",
                "ASDFGHJKL学",
                "ZXCVBNM警-+"
        );
        mLayouts.put(mkKey(CIVIL_SPEC, 2), spec);
        mLayouts.put(mkKey(CIVIL_SPEC, 3), spec);
        mLayouts.put(mkKey(CIVIL_SPEC, 4), spec);
        mLayouts.put(mkKey(CIVIL_SPEC, 5), spec);
        mLayouts.put(mkKey(CIVIL_SPEC, 6), spec);
        mLayouts.put(mkKey(CIVIL_SPEC, 7), spec);

        //// 全键盘
        mLayouts.put(mkKey(FULL, 0), createRows(
                "京津晋冀蒙辽吉黑沪苏",
                "浙皖闽赣鲁豫鄂湘粤桂",
                "琼渝川贵云藏陕甘青宁",
                "新民使123WQZVE",
                "HKBSLJNGC-"
        ));
        mLayouts.put(mkKey(FULL, 1), createRows(
                "1234567890",
                "QWERTYUP港澳",
                "ASDFGHJKL",
                "ZXCVBNM学警",
                "领使航挂试超-+"
        ));
        final List<List<KeyEntry>> full = createRows(
                "1234567890",
                "QWERTYUP港澳",
                "ASDFGHJKL学",
                "ZXCVBNM警",
                "领使挂试超-+"
        );
        mLayouts.put(mkKey(FULL, 2), full);
        mLayouts.put(mkKey(FULL, 3), full);
        mLayouts.put(mkKey(FULL, 4), full);
        mLayouts.put(mkKey(FULL, 5), full);
        mLayouts.put(mkKey(FULL, 6), full);
        mLayouts.put(mkKey(FULL, 7), full);
    }

    public List<List<KeyEntry>> getAvailable(KeyboardType keyboardType, int selectIndex) {
        final List<List<KeyEntry>> found = mLayouts.get(mkKey(keyboardType, selectIndex));
        if (null != found) {
            return found;
        } else {
            return Collections.emptyList();
        }
    }

    private static List<List<KeyEntry>> createRows(String... rows) {
        final List<List<KeyEntry>> layout = new ArrayList<>(rows.length);
        for (String keys : rows) {
            layout.add(mkEntitiesOf(keys));
        }
        return layout;
    }

    private static String mkKey(KeyboardType keyboardType, int selectIndex) {
        return "@" + keyboardType.name() + "." + selectIndex;
    }

}
