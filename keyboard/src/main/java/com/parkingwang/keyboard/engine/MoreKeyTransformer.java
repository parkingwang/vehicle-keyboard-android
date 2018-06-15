package com.parkingwang.keyboard.engine;

import static com.parkingwang.keyboard.engine.NumberType.AVIATION;
import static com.parkingwang.keyboard.engine.NumberType.CIVIL;
import static com.parkingwang.keyboard.engine.NumberType.HK_MACAO;
import static com.parkingwang.keyboard.engine.NumberType.LING2012;
import static com.parkingwang.keyboard.engine.NumberType.LING2018;
import static com.parkingwang.keyboard.engine.NumberType.NEW_ENERGY;
import static com.parkingwang.keyboard.engine.NumberType.PLA2012;
import static com.parkingwang.keyboard.engine.NumberType.SHI2012;
import static com.parkingwang.keyboard.engine.NumberType.WJ2012;

/**
 * 禁用更多键的逻辑：
 * 1. 第1位，民用、新能源、港澳、新旧领事馆
 * 2. 第3位，武警类型；
 * 3. 第7位，新能源、武警、军队、旧式2012大使馆、民航类型；
 * 4. 第8位；
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class MoreKeyTransformer extends LayoutMixer.AbstractTypedKeyTransformer {

    public MoreKeyTransformer() {
        super(KeyType.FUNC_MORE);
    }

    @Override
    protected KeyEntry transform(Context ctx, KeyEntry key) {
        if (0 == ctx.selectIndex && ctx.numberType.isAnyOf(CIVIL, NEW_ENERGY, HK_MACAO, LING2012, LING2018)) {
            return KeyEntry.newOfSetEnable(key, false);
        } else if (2 == ctx.selectIndex && WJ2012.equals(ctx.numberType)) {
            return KeyEntry.newOfSetEnable(key, false);
        } else if (6 == ctx.selectIndex && ctx.numberType.isAnyOf(NEW_ENERGY, WJ2012, PLA2012, SHI2012, AVIATION)) {
            return KeyEntry.newOfSetEnable(key, false);
        } else if (7 == ctx.selectIndex) {
            return KeyEntry.newOfSetEnable(key, false);
        } else {
            return key;
        }
    }

}
