package com.parkingwang.vehiclekeyboard.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 布局和键位混合器
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class Mixer {

    public static class Env {
        final String presetNumber;
        final int selectIndex;
        final NumberType numberType;
        final int limitLength;
        final List<KeyEntry> availableKeys;
        final List<List<KeyEntry>> layout;

        public Env(String presetNumber, int selectIndex, NumberType numberType,
                   int limitLength, List<KeyEntry> availableKeys, List<List<KeyEntry>> layout) {
            this.presetNumber = presetNumber;
            this.selectIndex = selectIndex;
            this.numberType = numberType;
            this.limitLength = limitLength;
            this.availableKeys = availableKeys;
            this.layout = layout;
        }
    }

    public interface Mapper {
        KeyEntry map(Env env, KeyEntry key);
    }

    private final List<Mapper> mMappers = new ArrayList<>();

    public List<List<KeyEntry>> mix(Env env) {
        final List<List<KeyEntry>> output = new ArrayList<>();
        for (List<KeyEntry> layoutRow : env.layout) {
            final List<KeyEntry> row = new ArrayList<>(layoutRow.size());
            for (KeyEntry item : layoutRow) {
                KeyEntry key = item;
                for (Mapper mapper : mMappers) {
                    final KeyEntry ret = mapper.map(env, key);
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

    public void addMapper(Mapper mapper) {
        mMappers.add(mapper);
    }
}
