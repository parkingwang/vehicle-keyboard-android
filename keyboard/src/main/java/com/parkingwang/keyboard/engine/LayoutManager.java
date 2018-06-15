package com.parkingwang.keyboard.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.parkingwang.keyboard.engine.NumberType.AVIATION;
import static com.parkingwang.keyboard.engine.NumberType.CIVIL;
import static com.parkingwang.keyboard.engine.NumberType.HK_MACAO;
import static com.parkingwang.keyboard.engine.NumberType.LING2012;
import static com.parkingwang.keyboard.engine.NumberType.LING2018;
import static com.parkingwang.keyboard.engine.NumberType.NEW_ENERGY;
import static com.parkingwang.keyboard.engine.NumberType.PLA2012;
import static com.parkingwang.keyboard.engine.NumberType.SHI2012;
import static com.parkingwang.keyboard.engine.NumberType.SHI2017;
import static com.parkingwang.keyboard.engine.NumberType.WJ2012;
import static com.parkingwang.keyboard.engine.Utils.mkEntitiesOf;


/**
 * 车牌号码类型对应的布局管理
 *
 * @author 陈哈哈 yoojiachen@gmail.com
 */
class LayoutManager {

    private final static String NAME_PROVINCE = "layout.province";
    private final static String NAME_FIRST = "layout.first.spec";
    private final static String NAME_LAST = "layout.last.spec";
    private final static String NAME_WITH_IO = "layout.with.io";
    private final static String NAME_WITHOUT_IO = "layout.without.io";
    private final Map<String, LayoutEntry> mNamedLayouts = new HashMap<>();
    private final List<LayoutProvider> mProviders = new ArrayList<>(5);

    LayoutManager() {
        // 省份简称布局
        mNamedLayouts.put(NAME_PROVINCE, createRows(
                "京津晋冀蒙辽吉黑沪苏",
                "浙皖闽赣鲁豫鄂湘粤桂",
                "琼渝川贵云藏陕甘",
                "青宁新台" + VNumberChars.MORE + "-+"
        ));

        // 首位特殊字符布局
        mNamedLayouts.put(NAME_FIRST, createRows(
                "1234567890",
                "QWERTYCVBN",
                "ASDFGHJKL",
                "ZX民使" + VNumberChars.BACK + "-+"
        ));

        // 带IO字母+数字
        mNamedLayouts.put(NAME_WITH_IO, createRows(
                "1234567890",
                "QWERTYUIOP",
                "ASDFGHJKLM",
                "ZXCVBN-+"
        ));

        // 末位特殊字符
        mNamedLayouts.put(NAME_LAST, createRows(
                "学警港澳航挂试超使领",
                "1234567890",
                "ABCDEFGHJK",
                "WXYZ" + VNumberChars.BACK + "-+"
        ));

        // 无IO字母+数字
        mNamedLayouts.put(NAME_WITHOUT_IO, createRows(
                "1234567890",
                "QWERTYUPMN",
                "ASDFGHJKLB",
                "ZXCV" + VNumberChars.MORE + "-+"
        ));

        mProviders.add(new ProvinceLayoutProvider());
        mProviders.add(new FirstSpecLayoutProvider());
        mProviders.add(new WithIOLayoutProvider());
        mProviders.add(new LastSpecLayoutProvider());
        mProviders.add(new WithoutIOLayoutProvider());
    }

    private static LayoutEntry createRows(String... rows) {
        final LayoutEntry layout = new LayoutEntry(rows.length);
        for (String keys : rows) {
            layout.add(mkEntitiesOf(keys));
        }
        return layout;
    }

    public LayoutEntry getLayout(Context ctx) {
        LayoutEntry layout = null;
        for (LayoutProvider provider : mProviders) {
            layout = provider.get(ctx);
            if (null != layout) {
                break;
            }
        }
        return layout;
    }

    interface LayoutProvider {
        LayoutEntry get(Context ctx);
    }

    /**
     * 省份简称布局提供器。
     * 1. 第1位，未知类型，非特殊状态；
     * 2. 第1位，民用、新能源、港澳、新旧领事馆类型；
     * 3. 第3位，武警类型；
     */
    final class ProvinceLayoutProvider implements LayoutProvider {
        @Override
        public LayoutEntry get(Context ctx) {
            if (0 == ctx.selectIndex || 2 == ctx.selectIndex) {
                if (0 == ctx.selectIndex && NumberType.AUTO_DETECT.equals(ctx.numberType) && !ctx.reqSpecLayout) {
                    return mNamedLayouts.get(NAME_PROVINCE);
                } else if (0 == ctx.selectIndex && ctx.numberType.isAnyOf(CIVIL, NEW_ENERGY, HK_MACAO, LING2012, LING2018)) {
                    return mNamedLayouts.get(NAME_PROVINCE);
                } else if (2 == ctx.selectIndex && NumberType.WJ2012.equals(ctx.numberType)) {
                    return mNamedLayouts.get(NAME_PROVINCE);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * 首位特殊字符布局提供器。
     * 1. 第1位，未知类型，且进入特殊布局状态；
     * 1. 第1位，武警、军队、新旧使馆类型、民航类型；
     */
    final class FirstSpecLayoutProvider implements LayoutProvider {

        @Override
        public LayoutEntry get(Context ctx) {
            if (0 == ctx.selectIndex) {
                if (ctx.numberType.isAnyOf(WJ2012, PLA2012, SHI2012, SHI2017, AVIATION)) {
                    return mNamedLayouts.get(NAME_FIRST);
                } else if (ctx.reqSpecLayout) {
                    return mNamedLayouts.get(NAME_FIRST);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * 带IO字母+数字布局提供器。
     * 1. 第4-6位；
     * 2. 第2位，非民航类型；
     * 3. 第3位，非武警类型；
     */
    final class WithIOLayoutProvider implements LayoutProvider {

        @Override
        public LayoutEntry get(Context ctx) {
            if (3 == ctx.selectIndex || 4 == ctx.selectIndex || 5 == ctx.selectIndex) {
                return mNamedLayouts.get(NAME_WITH_IO);
            } else if (1 == ctx.selectIndex && !AVIATION.equals(ctx.numberType)) {
                return mNamedLayouts.get(NAME_WITH_IO);
            } else if (2 == ctx.selectIndex && !WJ2012.equals(ctx.numberType)) {
                return mNamedLayouts.get(NAME_WITH_IO);
            } else {
                return null;
            }
        }
    }

    /**
     * 末位特殊字符布局提供器。
     * 1. 第2位，民航车牌类型；
     * 2. 第7位，进入特殊布局状态；
     * 3. 第7位，港澳、新2017式大使馆、新旧领事馆类型；
     */
    final class LastSpecLayoutProvider implements LayoutProvider {

        @Override
        public LayoutEntry get(Context ctx) {
            if (1 == ctx.selectIndex) {
                return mNamedLayouts.get(NAME_LAST);
            } else if (6 == ctx.selectIndex) {
                if (ctx.numberType.isAnyOf(HK_MACAO, SHI2017, LING2012, LING2018)) {
                    return mNamedLayouts.get(NAME_LAST);
                } else if (ctx.reqSpecLayout) {
                    return mNamedLayouts.get(NAME_LAST);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    ////

    /**
     * 无IO字符+数字布局提供器。
     * 1. 第7位，民用类型，非特殊布局状态；
     * 2. 第7位，新能源、武警、军队、旧2012式大使馆、民航；
     * 3. 第8位；
     */
    final class WithoutIOLayoutProvider implements LayoutProvider {

        @Override
        public LayoutEntry get(Context ctx) {
            if (6 == ctx.selectIndex) {
                if (NumberType.CIVIL.equals(ctx.numberType) && !ctx.reqSpecLayout) {
                    return mNamedLayouts.get(NAME_WITHOUT_IO);
                } else if (ctx.numberType.isAnyOf(NEW_ENERGY, WJ2012, PLA2012, SHI2012, AVIATION)) {
                    return mNamedLayouts.get(NAME_WITHOUT_IO);
                } else {
                    return null;
                }
            } else if (7 == ctx.selectIndex) {
                return mNamedLayouts.get(NAME_WITHOUT_IO);
            } else {
                return null;
            }
        }
    }

}
