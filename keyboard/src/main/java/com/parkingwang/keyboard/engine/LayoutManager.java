package com.parkingwang.keyboard.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.parkingwang.keyboard.engine.SpecIndex.MORE_POSTFIX;
import static com.parkingwang.keyboard.engine.SpecIndex.MORE_PREFIX;
import static com.parkingwang.keyboard.engine.Utils.mkEntitiesOf;


/**
 * 车牌号码类型对应的布局管理
 *
 * @author 陈哈哈 yoojiachen@gmail.com
 */
class LayoutManager {

    ////

    private final Map<String, List<List<KeyEntry>>> mPresetLayouts = new HashMap<>();

    LayoutManager() {
        // 军用车牌: QVKHBSLJNGCEZ
        mPresetLayouts.put(keyAt(MORE_PREFIX), createRows(
                "123456789",
                "QWEYCVBN0",
                "ASDFGHJKL",
                "ZX民使" + VNumberChars.BACK + "-+"
        ));

        // 第2位/末位可以选择的字符
        mPresetLayouts.put(keyAt(MORE_POSTFIX), createRows(
                "学警港澳使领航挂试超",
                "1234567890",
                "ABCDEFGHJK",
                "WXYZ" + VNumberChars.BACK + "-+"
        ));

        //// 民用键盘
        mPresetLayouts.put(keyAt(0), createRows(
                "京津晋冀蒙辽吉黑沪",
                "苏浙皖闽赣鲁豫鄂湘",
                "粤桂琼渝川贵云藏陕",
                "甘青宁新" + VNumberChars.MORE + "-+"
        ));
        mPresetLayouts.put(keyAt(1), createRows(
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
        mPresetLayouts.put(keyAt(2), civil);
        mPresetLayouts.put(keyAt(3), civil);
        mPresetLayouts.put(keyAt(4), civil);
        mPresetLayouts.put(keyAt(5), civil);
        mPresetLayouts.put(keyAt(6), civil);
        mPresetLayouts.put(keyAt(7), civil);
    }

    /**
     * 获取键盘布局
     *
     * @param context         环境变量
     * @param selectIndex 当前选中序号
     * @return 键盘布局
     */
    public List<List<KeyEntry>> layout(Context context, int selectIndex) {
        context.isSpecialLayout = false;
        // 动态的键盘布局
        switch (context.selectIndex) {
            case 0:
                switch (context.numberType) {
                    case SHI2012:
                    case SHI2017:
                    case PLA2012:
                    case WJ2012:
                    case AVIATION:
                        context.isSpecialLayout = true;
                        return cached(MORE_PREFIX);
                    default:
                        return cached(selectIndex);
                }

                // 第2位，特殊情况：民航
            case 1:
                if (NumberType.AVIATION.equals(context.numberType)) {
                    context.isSpecialLayout = true;
                    return cached(MORE_POSTFIX);
                } else {
                    return cached(selectIndex);
                }

                // 第3位，特殊情况：武警车牌省份简称
            case 2:
                if (NumberType.WJ2012.equals(context.numberType)) {
                    // 序号 0： 省份简称的布局
                    context.isSpecialLayout = true;
                    return cached(0);
                } else {
                    return cached(selectIndex);
                }

                // 第7位，特殊情况：
                // - 港澳车牌
                // - 新式大使馆
            case 6:
                switch (context.numberType) {
                    case HK_MACAO:
                    case SHI2017:
                    case LING2012:
                    case LING2018:
                        context.isSpecialLayout = true;
                        return cached(MORE_POSTFIX);
                    default:
                        return cached(selectIndex);
                }

            default:
                return cached(selectIndex);
        }
    }

    ////

    private List<List<KeyEntry>> cached(int selectIndex) {
        final List<List<KeyEntry>> found = mPresetLayouts.get(keyAt(selectIndex));
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
