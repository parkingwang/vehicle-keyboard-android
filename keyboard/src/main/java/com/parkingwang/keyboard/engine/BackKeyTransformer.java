package com.parkingwang.keyboard.engine;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class BackKeyTransformer extends Mixer.AbstractTypedKeyTransformer {

    public BackKeyTransformer() {
        super(KeyType.FUNC_BACK);
    }

    @Override
    protected KeyEntry transform(Context context, KeyEntry key) {
        return new KeyEntry(key.text, key.keyType,
                !context.isSpecialLayout);
    }
}
