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

    private Map<String, List<KeyEntry>> mKeys = new HashMap<>();

    PrepareKeyRegistry() {
        //// 民用车牌
        final List<KeyEntry> lettersNumeric = mkEntitiesOf(QWERTY_no_O + NUMERIC);
        final List<KeyEntry> civilProvince = mkEntitiesOf(CIVIL_PROVINCES);
        final List<KeyEntry> lettersHasO = mkEntitiesOf(QWERTY_has_O);
        final List<KeyEntry> civilPost = append(lettersNumeric, mkEntitiesOf(CIVIL_POST));
        mKeys.put(mkKey(CIVIL, 0), civilProvince);
        mKeys.put(mkKey(CIVIL, 1), lettersHasO);
        mKeys.put(mkKey(CIVIL, 2), lettersNumeric);
        mKeys.put(mkKey(CIVIL, 3), lettersNumeric);
        mKeys.put(mkKey(CIVIL, 4), lettersNumeric);
        mKeys.put(mkKey(CIVIL, 5), lettersNumeric);
        mKeys.put(mkKey(CIVIL, 6), civilPost);

        //// 新能源车牌
        final List<KeyEntry> numericDF = mkEntitiesOf(NUMERIC + "DF");
        mKeys.put(mkKey(NEW_ENERGY, 0), civilProvince);
        mKeys.put(mkKey(NEW_ENERGY, 1), lettersHasO);
        mKeys.put(mkKey(NEW_ENERGY, 2), numericDF);
        mKeys.put(mkKey(NEW_ENERGY, 3), lettersNumeric);
        mKeys.put(mkKey(NEW_ENERGY, 4), lettersNumeric);
        mKeys.put(mkKey(NEW_ENERGY, 5), lettersNumeric);
        mKeys.put(mkKey(NEW_ENERGY, 7), numericDF);

        //// 港澳车牌
        mKeys.put(mkKey(HK_MACAO, 0), civilProvince);
        mKeys.put(mkKey(HK_MACAO, 1), mkEntitiesOf("Z"));
        mKeys.put(mkKey(HK_MACAO, 2), lettersNumeric);
        mKeys.put(mkKey(HK_MACAO, 3), lettersNumeric);
        mKeys.put(mkKey(HK_MACAO, 4), lettersNumeric);
        mKeys.put(mkKey(HK_MACAO, 5), lettersNumeric);
        mKeys.put(mkKey(HK_MACAO, 6), mkEntitiesOf(CHARS_HK_MACAO));

        //// 武警2012式
        mKeys.put(mkKey(WJ2012, 0), mkEntitiesOf("W"));
        mKeys.put(mkKey(WJ2012, 1), mkEntitiesOf("J"));
        mKeys.put(mkKey(WJ2012, 2), civilProvince);
        mKeys.put(mkKey(WJ2012, 3), lettersNumeric);
        mKeys.put(mkKey(WJ2012, 4), lettersNumeric);
        mKeys.put(mkKey(WJ2012, 5), lettersNumeric);
        mKeys.put(mkKey(WJ2012, 6), lettersNumeric);
        mKeys.put(mkKey(WJ2012, 7), mkEntitiesOf(NUMERIC + "XBTSHJD"));

        //// 2017式大使馆
        final List<KeyEntry> numeric = mkEntitiesOf(NUMERIC);
        final List<KeyEntry> numeric123 = mkEntitiesOf(NUMERIC_123);
        mKeys.put(mkKey(SHI2017, 0), numeric123);
        mKeys.put(mkKey(SHI2017, 1), numeric);
        mKeys.put(mkKey(SHI2017, 2), numeric);
        mKeys.put(mkKey(SHI2017, 3), lettersNumeric);
        mKeys.put(mkKey(SHI2017, 4), lettersNumeric);
        mKeys.put(mkKey(SHI2017, 5), lettersNumeric);
        mKeys.put(mkKey(SHI2017, 6), mkEntitiesOf("使"));

        //// 2012式大使馆
        mKeys.put(mkKey(SHI2012, 0), mkEntitiesOf("使"));
        mKeys.put(mkKey(SHI2012, 1), numeric123);
        mKeys.put(mkKey(SHI2012, 2), numeric);
        mKeys.put(mkKey(SHI2012, 3), numeric);
        mKeys.put(mkKey(SHI2012, 4), lettersNumeric);
        mKeys.put(mkKey(SHI2012, 5), lettersNumeric);
        mKeys.put(mkKey(SHI2012, 6), lettersNumeric);

        //// 2012式军牌
        final List<KeyEntry> pla2012_0 = mkEntitiesOf(PLA2012_IDX0);
        mKeys.put(mkKey(PLA2012, 0), pla2012_0);
        mKeys.put(mkKey(PLA2012, 1), lettersHasO);
        mKeys.put(mkKey(PLA2012, 2), lettersNumeric);
        mKeys.put(mkKey(PLA2012, 3), lettersNumeric);
        mKeys.put(mkKey(PLA2012, 4), lettersNumeric);
        mKeys.put(mkKey(PLA2012, 5), lettersNumeric);
        mKeys.put(mkKey(PLA2012, 6), lettersNumeric);

        //// 2012式领事馆
        mKeys.put(mkKey(LING2018, 0), civilProvince);
        mKeys.put(mkKey(LING2018, 1), lettersHasO);
        mKeys.put(mkKey(LING2018, 2), lettersNumeric);
        mKeys.put(mkKey(LING2018, 3), lettersNumeric);
        mKeys.put(mkKey(LING2018, 4), lettersNumeric);
        mKeys.put(mkKey(LING2018, 5), lettersNumeric);
        mKeys.put(mkKey(LING2018, 6), mkEntitiesOf("领"));

        //// 2018式领事馆
        mKeys.put(mkKey(LING2018, 0), civilProvince);
        mKeys.put(mkKey(LING2018, 1), numeric123);
        mKeys.put(mkKey(LING2018, 2), numeric);
        mKeys.put(mkKey(LING2018, 3), numeric);
        mKeys.put(mkKey(LING2018, 4), lettersNumeric);
        mKeys.put(mkKey(LING2018, 5), lettersNumeric);
        mKeys.put(mkKey(LING2018, 6), mkEntitiesOf("领"));

        //// 民航
        mKeys.put(mkKey(AVIATION, 0), mkEntitiesOf("民"));
        mKeys.put(mkKey(AVIATION, 1), mkEntitiesOf("航"));
        mKeys.put(mkKey(AVIATION, 2), lettersNumeric);
        mKeys.put(mkKey(AVIATION, 3), lettersNumeric);
        mKeys.put(mkKey(AVIATION, 4), lettersNumeric);
        mKeys.put(mkKey(AVIATION, 5), lettersNumeric);
        mKeys.put(mkKey(AVIATION, 6), lettersNumeric);

        //// 未知类型
        mKeys.put(mkKey(AUTO_DETECT, 0), append(civilProvince, pla2012_0, mkEntitiesOf("民使W123")));
        mKeys.put(mkKey(AUTO_DETECT, 1), append(lettersHasO, mkEntitiesOf("航J")));
        mKeys.put(mkKey(AUTO_DETECT, 2), lettersNumeric);
        mKeys.put(mkKey(AUTO_DETECT, 3), lettersNumeric);
        mKeys.put(mkKey(AUTO_DETECT, 4), lettersNumeric);
        mKeys.put(mkKey(AUTO_DETECT, 5), lettersNumeric);
        mKeys.put(mkKey(AUTO_DETECT, 6), civilPost);
    }

    /**
     * 根据指定车牌号码类型和位置，返回当前可用的全部键位
     *
     * @param type          车牌类型
     * @param selectedIndex 当前选择的位置
     * @return 全部可用键位
     */
    public List<KeyEntry> available(NumberType type, int selectedIndex) {
        final List<KeyEntry> found = mKeys.get(mkKey(type, selectedIndex));
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
