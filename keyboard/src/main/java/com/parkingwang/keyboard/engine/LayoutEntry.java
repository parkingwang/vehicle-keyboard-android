package com.parkingwang.keyboard.engine;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
final public class LayoutEntry extends ArrayList<RowEntry> {
    public LayoutEntry(int initialCapacity) {
        super(initialCapacity);
    }

    public LayoutEntry() {
    }

    public LayoutEntry(@NonNull Collection<? extends RowEntry> c) {
        super(c);
    }
}
