package com.parkingwang.vehiclekeyboard.core;

import static com.parkingwang.vehiclekeyboard.core.VNumberChars.BACK;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.DEL;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.MORE;
import static com.parkingwang.vehiclekeyboard.core.VNumberChars.OK;

/**
 * 通用的键位转换器
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class GeneralKeyMapper implements Mixer.Mapper {
    @Override
    public KeyEntry map(Env env, KeyEntry key) {
        final boolean enabled;

        final String text;
        switch (key.text) {
            case OK:
                text = "确定";
                // 全部车牌号码已输完，启用
                enabled = (env.limitLength == env.presetNumber.length());
                break;

            case DEL:
                text = "删除";
                // 删除逻辑：当预设车牌号码不是空，则启用
                enabled = (0 != env.presetNumber.length());
                break;

            case MORE:
                text = "更多";
                enabled = true;
                break;

            case BACK:
                text = "返回";
                enabled = true;
                break;

            default:
                text = key.text;
                enabled = env.availableKeys.contains(key);
                break;

        }
        return new KeyEntry(text, key.keyType, enabled, key.isFunKey);
    }

}
