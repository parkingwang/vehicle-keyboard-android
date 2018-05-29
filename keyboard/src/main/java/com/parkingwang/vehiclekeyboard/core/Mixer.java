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
        final List<KeyEntry> keys;
        final List<List<KeyEntry>> layout;

        public Env(String presetNumber, int selectIndex, NumberType numberType,
                   int limitLength, List<KeyEntry> keys, List<List<KeyEntry>> layout) {
            this.presetNumber = presetNumber;
            this.selectIndex = selectIndex;
            this.numberType = numberType;
            this.limitLength = limitLength;
            this.keys = keys;
            this.layout = layout;
        }
    }

    public interface Filter {
        KeyEntry filter(Env env, KeyEntry key);
    }

    private final List<Filter> mFilters = new ArrayList<>();

    public List<List<KeyEntry>> mix(Env env) {
        final List<List<KeyEntry>> output = new ArrayList<>();
        for (List<KeyEntry> rows : env.layout) {
            final List<KeyEntry> newRow = new ArrayList<>(rows.size());
            for (KeyEntry r : rows) {
                KeyEntry key = r;
                for (Filter filter : mFilters) {
                    final KeyEntry ret = filter.filter(env, key);
                    if (null != ret) {
                        key = ret;
                    }
                }
                newRow.add(key);
            }
            output.add(newRow);
        }
        return output;
    }

    public void addFilter(Filter filter) {
        mFilters.add(filter);
    }
}
