package com.parkingwang.keyboard.engine;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class RemoveOKLayoutTransformer implements LayoutMixer.LayoutTransformer {

    private boolean mRemoveEnabled = false;

    public void setRemoveEnabled(boolean removeEnabled) {
        mRemoveEnabled = removeEnabled;
    }

    @Override
    public LayoutEntry transformLayout(Context context, LayoutEntry layout) {
        if (mRemoveEnabled) {
            // 查找最后一行最后的“确定”键位，删除
            final int rows = layout.size();
            if (rows == 0) {
                return null;
            }
            final RowEntry lastRow = layout.get(rows - 1);
            final int keys = lastRow.size();
            if (keys == 0) {
                return null;
            }

            for (int i = keys - 1; i >= 0; i--) {
                KeyEntry key = lastRow.get(i);
                if (KeyType.FUNC_OK.equals(key.keyType)) {
                    lastRow.remove(i);
                    break;
                }
            }
            return layout;
        } else {
            return null;
        }
    }
}
