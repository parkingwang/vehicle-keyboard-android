package com.parkingwang.keyboard.engine;

import static com.parkingwang.keyboard.engine.VNumberChars.BACK;
import static com.parkingwang.keyboard.engine.VNumberChars.DEL;
import static com.parkingwang.keyboard.engine.VNumberChars.MORE;
import static com.parkingwang.keyboard.engine.VNumberChars.OK;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class FuncKeyTransformer implements LayoutMixer.KeyTransformer {
    @Override
    public KeyEntry transformKey(Context context, KeyEntry key) {
        final boolean enabled;
        final String text;
        switch (key.text) {
            case OK:
                text = "确定";
                // 全部车牌号码已输完，启用
                enabled = (context.limitLength == context.presetNumber.length());
                break;

            case DEL:
                text = "删除";
                // 删除逻辑：当预设车牌号码不是空，则启用
                enabled = (0 != context.presetNumber.length());
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
                enabled = context.availableKeys.contains(key);
                break;

        }
        return new KeyEntry(text, key.keyType, enabled);
    }

}
