package com.parkingwang.vehiclekeyboard.core;

import static com.parkingwang.vehiclekeyboard.core.VNumberChars.CHARS_HK_MACAO;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class HKMacaoKeyFilter implements Mixer.Filter {

    @Override
    public KeyEntry filter(Mixer.Env env, KeyEntry key) {
        // 当选择第7位时，判断是否可以启用港澳车牌
        if (env.selectIndex == 6 && !key.isFunKey) {
            final boolean enabled;
            final boolean isHKMacao = CHARS_HK_MACAO.contains(key.text);
            if (env.presetNumber.startsWith("粤Z")) {
                enabled = isHKMacao;
            } else {
                enabled = !isHKMacao;
            }
            return new KeyEntry(key.text, key.keyType, enabled, false);
        } else {
            return null;
        }
    }
}
