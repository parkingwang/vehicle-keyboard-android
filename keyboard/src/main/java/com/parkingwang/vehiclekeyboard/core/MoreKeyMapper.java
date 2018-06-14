package com.parkingwang.vehiclekeyboard.core;

/**
 * 更多键位的启用与禁用逻辑
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class MoreKeyMapper implements Mixer.Mapper {
    @Override
    public KeyEntry map(Env env, KeyEntry key) {
        if (KeyType.FUNC_MORE.equals(key.keyType)) {
            // // 在第1位、第2位、以及第7位，可以选择更多。其它位置不可以。
            final boolean positionAllowed = env.selectIndex == 0 || env.selectIndex == 6;
            if (!positionAllowed) {
                return mkEnable(key, false);
            } else {
                return mkEnable(key,
                        NumberType.AUTO_DETECT.equals(env.numberType) ||
                                NumberType.CIVIL.equals(env.numberType));
            }
        } else {
            return key;
        }
    }

    private static KeyEntry mkEnable(KeyEntry key, boolean enabled) {
        return new KeyEntry(key.text, key.keyType, enabled, key.isFunKey);
    }
}
