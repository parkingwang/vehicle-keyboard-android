package com.parkingwang.vehiclekeyboard.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.parkingwang.vehiclekeyboard.core.NumberType.AUTO_DETECT;
import static com.parkingwang.vehiclekeyboard.core.NumberType.AVIATION;
import static com.parkingwang.vehiclekeyboard.core.NumberType.CIVIL;
import static com.parkingwang.vehiclekeyboard.core.NumberType.HK_MACAO;
import static com.parkingwang.vehiclekeyboard.core.NumberType.LING2018;
import static com.parkingwang.vehiclekeyboard.core.NumberType.NEW_ENERGY;
import static com.parkingwang.vehiclekeyboard.core.NumberType.PLA2012;
import static com.parkingwang.vehiclekeyboard.core.NumberType.SHI2012;
import static com.parkingwang.vehiclekeyboard.core.NumberType.SHI2017;
import static com.parkingwang.vehiclekeyboard.core.NumberType.WJ2012;
import static com.parkingwang.vehiclekeyboard.core.Utils.append;
import static com.parkingwang.vehiclekeyboard.core.Utils.mkEntitiesOf;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.CHARS_HK_MACAO;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.CIVIL_POST;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.CIVIL_PROVINCES;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.NUMERIC;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.NUMERIC_123;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.PLA2012_IDX0;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.QWERTY_has_O;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.QWERTY_no_O;

/**
 * 键盘布局注册管理器。
 * 根据当前指定车牌号码类型、选中序号，返回当前可用的全部键位数据。
 *
 * @author 陈哈哈 yoojiachen@gmail.com
 */
class PrepareKeyRegistry {

    private final Map<String, List<KeyEntry>> mCache = new HashMap<>();

    PrepareKeyRegistry() {
        //// 民用车牌
        final List<KeyEntry> lettersNumeric = mkEntitiesOf(QWERTY_no_O + NUMERIC);
        final List<KeyEntry> civilProvince = mkEntitiesOf(CIVIL_PROVINCES);
        final List<KeyEntry> lettersHasO = mkEntitiesOf(QWERTY_has_O);
        final List<KeyEntry> civilPost = append(lettersNumeric, mkEntitiesOf(CIVIL_POST));
        mCache.put(mkKey(CIVIL, 0), civilProvince);
        mCache.put(mkKey(CIVIL, 1), lettersHasO);
        mCache.put(mkKey(CIVIL, 2), lettersNumeric);
        mCache.put(mkKey(CIVIL, 3), lettersNumeric);
        mCache.put(mkKey(CIVIL, 4), lettersNumeric);
        mCache.put(mkKey(CIVIL, 5), lettersNumeric);
        mCache.put(mkKey(CIVIL, 6), civilPost);
        mCache.put(mkKey(CIVIL, SpecIndex.MORE_POSTFIX), civilPost);

        //// 新能源车牌
        final List<KeyEntry> numericDF = mkEntitiesOf(NUMERIC + "DF");
        mCache.put(mkKey(NEW_ENERGY, 0), civilProvince);
        mCache.put(mkKey(NEW_ENERGY, 1), lettersHasO);
        mCache.put(mkKey(NEW_ENERGY, 2), numericDF);
        mCache.put(mkKey(NEW_ENERGY, 3), lettersNumeric);
        mCache.put(mkKey(NEW_ENERGY, 4), lettersNumeric);
        mCache.put(mkKey(NEW_ENERGY, 5), lettersNumeric);
        mCache.put(mkKey(NEW_ENERGY, 6), lettersNumeric);
        mCache.put(mkKey(NEW_ENERGY, 7), numericDF);

        //// 港澳车牌
        List<KeyEntry> hkMacao = mkEntitiesOf(CHARS_HK_MACAO);
        mCache.put(mkKey(HK_MACAO, 0), civilProvince);
        mCache.put(mkKey(HK_MACAO, 1), mkEntitiesOf("Z"));
        mCache.put(mkKey(HK_MACAO, 2), lettersNumeric);
        mCache.put(mkKey(HK_MACAO, 3), lettersNumeric);
        mCache.put(mkKey(HK_MACAO, 4), lettersNumeric);
        mCache.put(mkKey(HK_MACAO, 5), lettersNumeric);
        mCache.put(mkKey(HK_MACAO, 6), hkMacao);
        mCache.put(mkKey(HK_MACAO, SpecIndex.MORE_POSTFIX), hkMacao);

        //// 武警2012式
        mCache.put(mkKey(WJ2012, 0), mkEntitiesOf("W"));
        mCache.put(mkKey(WJ2012, 1), mkEntitiesOf("J"));
        mCache.put(mkKey(WJ2012, 2), civilProvince);
        mCache.put(mkKey(WJ2012, 3), lettersNumeric);
        mCache.put(mkKey(WJ2012, 4), lettersNumeric);
        mCache.put(mkKey(WJ2012, 5), lettersNumeric);
        mCache.put(mkKey(WJ2012, 6), lettersNumeric);
        mCache.put(mkKey(WJ2012, 7), mkEntitiesOf(NUMERIC + "XBTSHJD"));

        //// 2017式大使馆
        final List<KeyEntry> numeric = mkEntitiesOf(NUMERIC);
        final List<KeyEntry> numeric123 = mkEntitiesOf(NUMERIC_123);
        final List<KeyEntry> shi = mkEntitiesOf("使");
        mCache.put(mkKey(SHI2017, SpecIndex.MORE_PREFIX), shi);
        mCache.put(mkKey(SHI2017, 0), numeric123);
        mCache.put(mkKey(SHI2017, 1), numeric);
        mCache.put(mkKey(SHI2017, 2), numeric);
        mCache.put(mkKey(SHI2017, 3), lettersNumeric);
        mCache.put(mkKey(SHI2017, 4), lettersNumeric);
        mCache.put(mkKey(SHI2017, 5), lettersNumeric);
        mCache.put(mkKey(SHI2017, 6), shi);
        mCache.put(mkKey(SHI2017, SpecIndex.MORE_POSTFIX), shi);

        //// 2012式大使馆
        mCache.put(mkKey(SHI2012, SpecIndex.MORE_PREFIX), shi);
        mCache.put(mkKey(SHI2012, 0), shi);
        mCache.put(mkKey(SHI2012, 1), numeric123);
        mCache.put(mkKey(SHI2012, 2), numeric);
        mCache.put(mkKey(SHI2012, 3), numeric);
        mCache.put(mkKey(SHI2012, 4), lettersNumeric);
        mCache.put(mkKey(SHI2012, 5), lettersNumeric);
        mCache.put(mkKey(SHI2012, 6), lettersNumeric);

        //// 2012式军牌
        final List<KeyEntry> pla2012_0 = mkEntitiesOf(PLA2012_IDX0);
        mCache.put(mkKey(PLA2012, SpecIndex.MORE_PREFIX), pla2012_0);
        mCache.put(mkKey(PLA2012, 1), lettersHasO);
        mCache.put(mkKey(PLA2012, 2), lettersNumeric);
        mCache.put(mkKey(PLA2012, 3), lettersNumeric);
        mCache.put(mkKey(PLA2012, 4), lettersNumeric);
        mCache.put(mkKey(PLA2012, 5), lettersNumeric);
        mCache.put(mkKey(PLA2012, 6), lettersNumeric);

        //// 2012式领事馆
        final List<KeyEntry> ling = mkEntitiesOf("领");
        mCache.put(mkKey(LING2018, 0), civilProvince);
        mCache.put(mkKey(LING2018, 1), lettersHasO);
        mCache.put(mkKey(LING2018, 2), lettersNumeric);
        mCache.put(mkKey(LING2018, 3), lettersNumeric);
        mCache.put(mkKey(LING2018, 4), lettersNumeric);
        mCache.put(mkKey(LING2018, 5), lettersNumeric);
        mCache.put(mkKey(LING2018, SpecIndex.MORE_POSTFIX), ling);

        //// 2018式领事馆
        mCache.put(mkKey(LING2018, 0), civilProvince);
        mCache.put(mkKey(LING2018, 1), numeric123);
        mCache.put(mkKey(LING2018, 2), numeric);
        mCache.put(mkKey(LING2018, 3), numeric);
        mCache.put(mkKey(LING2018, 4), lettersNumeric);
        mCache.put(mkKey(LING2018, 5), lettersNumeric);
        mCache.put(mkKey(LING2018, 6), ling);

        //// 民航
        mCache.put(mkKey(AVIATION, 0), mkEntitiesOf("民"));
        mCache.put(mkKey(AVIATION, 1), mkEntitiesOf("航"));
        mCache.put(mkKey(AVIATION, 2), lettersNumeric);
        mCache.put(mkKey(AVIATION, 3), lettersNumeric);
        mCache.put(mkKey(AVIATION, 4), lettersNumeric);
        mCache.put(mkKey(AVIATION, 5), lettersNumeric);
        mCache.put(mkKey(AVIATION, 6), lettersNumeric);

        //// 未知类型
        final List<KeyEntry> auto = append(civilProvince, lettersNumeric, mkEntitiesOf("民使"));
        mCache.put(mkKey(AUTO_DETECT, SpecIndex.MORE_PREFIX), auto);
        mCache.put(mkKey(AUTO_DETECT, 0), auto);
        mCache.put(mkKey(AUTO_DETECT, 1), append(lettersHasO, mkEntitiesOf("航J")));
        mCache.put(mkKey(AUTO_DETECT, 2), lettersNumeric);
        mCache.put(mkKey(AUTO_DETECT, 3), lettersNumeric);
        mCache.put(mkKey(AUTO_DETECT, 4), lettersNumeric);
        mCache.put(mkKey(AUTO_DETECT, 5), lettersNumeric);
        mCache.put(mkKey(AUTO_DETECT, 6), civilPost);
    }

    /**
     * 根据指定车牌号码类型和位置，返回当前可用的全部键位
     *
     * @param type          车牌类型
     * @param selectedIndex 当前选择的位置
     * @return 全部可用键位
     */
    public List<KeyEntry> available(NumberType type, int selectedIndex) {
        final List<KeyEntry> found = mCache.get(mkKey(type, selectedIndex));
        if (null != found) {
            return found;
        } else {
            return Collections.emptyList();
        }
    }


    private static String mkKey(NumberType type, int index) {
        return "@" + type.name() + "." + index;
    }

}
