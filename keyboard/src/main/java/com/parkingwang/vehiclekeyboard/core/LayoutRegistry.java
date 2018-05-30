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

    private final Map<String, List<List<KeyEntry>>> mCache = new HashMap<>();

    LayoutRegistry() {
        //// 民用键盘
        mCache.put(mkKey(CIVIL_ONLY, 0), createRows(
                "京津晋冀蒙辽吉黑沪",
                "苏浙皖闽赣鲁豫鄂",
                "湘粤桂琼渝川贵云",
                "藏陕甘青宁新-+"
        ));
        mCache.put(mkKey(CIVIL_ONLY, 1), createRows(
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
        mCache.put(mkKey(CIVIL_ONLY, 2), civil);
        mCache.put(mkKey(CIVIL_ONLY, 3), civil);
        mCache.put(mkKey(CIVIL_ONLY, 4), civil);
        mCache.put(mkKey(CIVIL_ONLY, 5), civil);
        mCache.put(mkKey(CIVIL_ONLY, 6), civil);
        mCache.put(mkKey(CIVIL_ONLY, 7), civil);

        //// 民用特殊键盘
        mCache.put(mkKey(CIVIL_SPEC, 0), createRows(
                "京津晋冀蒙辽吉黑沪",
                "苏浙皖闽赣鲁豫鄂湘",
                "粤桂琼渝川贵云藏陕",
                "甘青宁新W-+"
        ));
        mCache.put(mkKey(CIVIL_SPEC, 1), createRows(
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
        mCache.put(mkKey(CIVIL_SPEC, 2), spec);
        mCache.put(mkKey(CIVIL_SPEC, 3), spec);
        mCache.put(mkKey(CIVIL_SPEC, 4), spec);
        mCache.put(mkKey(CIVIL_SPEC, 5), spec);
        mCache.put(mkKey(CIVIL_SPEC, 6), spec);
        mCache.put(mkKey(CIVIL_SPEC, 7), spec);

        //// 全键盘
        mCache.put(mkKey(FULL, 0), createRows(
                "京津晋冀蒙辽吉黑沪苏",
                "浙皖闽赣鲁豫鄂湘粤桂",
                "琼渝川贵云藏陕甘青宁",
                "新民使123WQZVE",
                "HKBSLJNGC-"
        ));
        mCache.put(mkKey(FULL, 1), createRows(
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
        mCache.put(mkKey(FULL, 2), full);
        mCache.put(mkKey(FULL, 3), full);
        mCache.put(mkKey(FULL, 4), full);
        mCache.put(mkKey(FULL, 5), full);
        mCache.put(mkKey(FULL, 6), full);
        mCache.put(mkKey(FULL, 7), full);
    }

    /**
     * 获取键盘布局
     *
     * @param env          环境变量
     * @param keyboardType 指定键盘布局类型
     * @param selectIndex  当前选中序号
     * @return 键盘布局
     */
    public List<List<KeyEntry>> layout(Env env, KeyboardType keyboardType, int selectIndex) {
        // 动态的键盘布局
        // 1. 武警第3位，要显示民用车牌的第1位布局（省份）；
        // 2. 其它按已注册缓存的来获取；
        if (selectIndex == 2 && NumberType.WJ2012.equals(env.numberType)) {
            return cached(KeyboardType.CIVIL_ONLY, 0);
        } else {
            return cached(keyboardType, selectIndex);
        }
    }

    private List<List<KeyEntry>> cached(KeyboardType keyboardType, int selectIndex) {
        final List<List<KeyEntry>> found = mCache.get(mkKey(keyboardType, selectIndex));
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
