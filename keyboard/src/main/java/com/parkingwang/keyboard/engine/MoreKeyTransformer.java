package com.parkingwang.keyboard.engine;

/**
 * 更多键位的启用与禁用逻辑
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class MoreKeyTransformer extends Mixer.AbstractTypedKeyTransformer {

    public MoreKeyTransformer() {
        super(KeyType.FUNC_MORE);
    }

    @Override
    protected KeyEntry transform(Context context, KeyEntry key) {
        // 在第1位、第2位、以及第7位，可以选择更多。其它位置不可以。
        final boolean positionAllowed = context.selectIndex == 0 || context.selectIndex == 6;
        if (!positionAllowed) {
            return mkEnable(key, false);
        } else {
            return mkEnable(key,
                    NumberType.AUTO_DETECT.equals(context.numberType) ||
                            NumberType.CIVIL.equals(context.numberType));
        }
    }

    private static KeyEntry mkEnable(KeyEntry key, boolean enabled) {
        return new KeyEntry(key.text, key.keyType, enabled);
    }
}
