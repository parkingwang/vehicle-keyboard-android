package com.parkingwang.keyboard.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * 布局和键位混合器
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class LayoutMixer {

    /**
     * 键位转换接口
     */
    public interface KeyTransformer {
        KeyEntry transformKey(Context context, KeyEntry key);
    }

    /**
     * 布局转换接口
     */
    public interface LayoutTransformer {
        LayoutEntry transformLayout(Context context, LayoutEntry layout);
    }

    private final List<KeyTransformer> mKeyTransformers = new ArrayList<>();
    private final List<LayoutTransformer> mLayoutTransformers = new ArrayList<>();

    /**
     * @param context
     * @param layout
     * @return
     */
    public LayoutEntry transform(Context context, LayoutEntry layout) {
        LayoutEntry out = layout;
        for (LayoutTransformer t : mLayoutTransformers) {
            final LayoutEntry ret = t.transformLayout(context, out);
            if (null != ret) {
                out = ret;
            }
        }
        return out;
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

    public void addKeyTransformer(KeyTransformer keyTransformer) {
        mKeyTransformers.add(keyTransformer);
    }

    public void addLayoutTransformer(LayoutTransformer transformer) {
        mLayoutTransformers.add(transformer);
    }

    ////

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
