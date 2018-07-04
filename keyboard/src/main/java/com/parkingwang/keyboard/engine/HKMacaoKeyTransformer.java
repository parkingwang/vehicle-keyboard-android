package com.parkingwang.keyboard.engine;

/**
 * 港澳车牌键位限定：
 * 当第一位是"粤"时，"港澳"按键才可点击。
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class HKMacaoKeyTransformer extends LayoutMixer.AbstractTypedKeyTransformer {

    public HKMacaoKeyTransformer() {
        super(KeyType.GENERAL);
    }

    @Override
    protected KeyEntry transform(Context ctx, KeyEntry key) {
        if (6 == ctx.selectIndex && "港澳".contains(key.text)) {
            return KeyEntry.newOfSetEnable(key, ctx.presetNumber.startsWith("粤"));
        }
        return key;
    }
}
