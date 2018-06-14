package com.parkingwang.vehiclekeyboard.core;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class BackKeyKeyTransformer extends Mixer.AbstractTypedKeyTransformer {

    public BackKeyKeyTransformer() {
        super(KeyType.FUNC_BACK);
    }

    @Override
    protected KeyEntry transform(Env env, KeyEntry key) {
        return null;
    }
}
