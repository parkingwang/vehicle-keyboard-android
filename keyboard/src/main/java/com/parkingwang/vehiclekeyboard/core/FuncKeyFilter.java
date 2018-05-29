package com.parkingwang.vehiclekeyboard.core;

import java.util.List;

import static com.parkingwang.vehiclekeyboard.core.VNumberChars.DEL;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.MORE;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.OK;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class FuncKeyFilter implements Mixer.Filter {
    @Override
    public KeyEntry filter(Mixer.Env env, KeyEntry key) {
        final boolean enabled;

        final String text;
        switch (key.text) {
            // 全部车牌号码已输完，启用
            case OK:
                text = "确定";
                enabled = (env.limitLength == env.presetNumber.length());
                break;

            // 删除逻辑：当预设车牌号码不是空，则启用
            case DEL:
                text = "删除";
                enabled = (0 != env.presetNumber.length());
                break;

            case MORE:
                text = "更多";
                enabled = true;
                break;

            default:
                text = key.text;
                enabled = isAvailable(env.keys, key);
                break;

        }
        return new KeyEntry(text, key.keyType, enabled, key.isFunKey);
    }

    private static boolean isAvailable(List<KeyEntry> availableKeys, KeyEntry key) {
        for (KeyEntry ak : availableKeys) {
            if (ak.text.equals(key.text)) {
                return true;
            }
        }
        return false;
    }
}
