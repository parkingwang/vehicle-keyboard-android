package com.parkingwang.vehiclekeyboard.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.parkingwang.vehiclekeyboard.core.SpecIndex.MORE_POSTFIX;
import static com.parkingwang.vehiclekeyboard.core.SpecIndex.MORE_PREFIX;
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
        // 军用车牌: QVKHBSLJNGCEZ
        mCache.put(keyAt(MORE_PREFIX), createRows(
                "123456789",
                "QWEYCVBN0",
                "ASDFGHJKL",
                "ZX民使" + VNumberChars.BACK + "-+"
        ));

        // 第2位/末位可以选择的字符
        mCache.put(keyAt(MORE_POSTFIX), createRows(
                "学警港澳使领航挂试超",
                "1234567890",
                "ABCDEFGHJK",
                "WXYZ" + VNumberChars.BACK + "-+"
        ));

        //// 民用键盘
        mCache.put(keyAt(0), createRows(
                "京津晋冀蒙辽吉黑沪",
                "苏浙皖闽赣鲁豫鄂湘",
                "粤桂琼渝川贵云藏陕",
                "甘青宁新" + VNumberChars.MORE + "-+"
        ));
        mCache.put(keyAt(1), createRows(
                "1234567890",
                "QWERTYUIOP",
                "ASDFGHJKLM",
                "ZXCVBN-+"
        ));
        final List<List<KeyEntry>> civil = createRows(
                "1234567890",
                "QWERTYUPMN",
                "ASDFGHJKLB",
                "ZXCV" + VNumberChars.MORE + "-+"
        );
        mCache.put(keyAt(2), civil);
        mCache.put(keyAt(3), civil);
        mCache.put(keyAt(4), civil);
        mCache.put(keyAt(5), civil);
        mCache.put(keyAt(6), civil);
        mCache.put(keyAt(7), civil);
    }

    /**
     * 获取键盘布局
     *
     * @param env          环境变量
     * @param selectIndex  当前选中序号
     * @return 键盘布局
     */
    public List<List<KeyEntry>> layout(Env env, int selectIndex) {
        // 动态的键盘布局
        switch (env.selectIndex) {
            default:
            case 0:
                return cached(selectIndex);

            // 第2位，特殊情况：民航
            case 1:
                if (NumberType.AVIATION.equals(env.numberType)) {
                    return cached(MORE_POSTFIX);
                } else {
                    return cached(selectIndex);
                }

                // 第3位，特殊情况：武警车牌省份简称
            case 2:
                if (NumberType.WJ2012.equals(env.numberType)) {
                    return cached(0);
                } else {
                    return cached(selectIndex);
                }
        }
    }

    private List<List<KeyEntry>> cached(int selectIndex) {
        final List<List<KeyEntry>> found = mCache.get(keyAt(selectIndex));
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

    private static String keyAt(int selectIndex) {
        return "@Layout." + selectIndex;
    }

}
