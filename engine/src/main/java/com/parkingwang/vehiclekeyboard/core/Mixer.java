package com.parkingwang.vehiclekeyboard.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 布局和键位混合器
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class Mixer {

    public interface Mapper {
        KeyEntry map(Env env, KeyEntry key);
    }

    private final List<Mapper> mMappers = new ArrayList<>();

    public List<List<KeyEntry>> mix(Env env, List<List<KeyEntry>> layout) {
        final List<List<KeyEntry>> output = new ArrayList<>();
        for (List<KeyEntry> layoutRow : layout) {
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
