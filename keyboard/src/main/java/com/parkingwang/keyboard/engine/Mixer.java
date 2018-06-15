package com.parkingwang.keyboard.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * 布局和键位混合器
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class Mixer {

    private final List<KeyTransformer> mKeyTransformers = new ArrayList<>();
    private boolean mRemoveFuncOK = false;

    /**
     * @param removeFuncOK 设置是否删除“确定”键
     */
    public void setRemoveFuncOK(boolean removeFuncOK) {
        mRemoveFuncOK = removeFuncOK;
    }

    /**
     * 使用键位变换器来处理布局
     *
     * @param context Env
     * @param layout  布局
     * @return 键位列表
     */
    public LayoutEntry mix(Context context, LayoutEntry layout) {
        final LayoutEntry output = new LayoutEntry();
        for (RowEntry layoutRow : layout) {
            final RowEntry row = new RowEntry(layoutRow.size());
            for (KeyEntry item : layoutRow) {
                KeyEntry key = item;
                if (mRemoveFuncOK && KeyType.FUNC_OK.equals(key.keyType)) {
                    continue;
                }
                for (KeyTransformer keyTransformer : mKeyTransformers) {
                    final KeyEntry ret = keyTransformer.transformKey(context, key);
                    if (null != ret) {
                        key = ret;
                    }
                }
                row.add(key);
            }
            output.add(row);
        }
        return output;
    }

    public void addMapper(KeyTransformer keyTransformer) {
        mKeyTransformers.add(keyTransformer);
    }

    public interface KeyTransformer {
        KeyEntry transformKey(Context context, KeyEntry key);
    }

    /**
     * 特定键位类型过滤处理
     */
    public static abstract class AbstractTypedKeyTransformer implements KeyTransformer {

        private final KeyType mKeyType;

        public AbstractTypedKeyTransformer(KeyType keyType) {
            mKeyType = keyType;
        }

        @Override
        final public KeyEntry transformKey(Context context, KeyEntry key) {
            if (mKeyType.equals(key.keyType)) {
                return transform(context, key);
            } else {
                return key;
            }
        }

        protected abstract KeyEntry transform(Context context, KeyEntry key);
    }
}
