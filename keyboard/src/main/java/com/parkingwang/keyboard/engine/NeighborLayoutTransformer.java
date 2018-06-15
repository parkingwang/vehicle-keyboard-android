package com.parkingwang.keyboard.engine;

import com.parkingwang.keyboard.neighbor.Province;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 周边省份键盘重新布局
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class NeighborLayoutTransformer implements LayoutMixer.LayoutTransformer {
    @Override
    public LayoutEntry transformLayout(Context context, LayoutEntry layout) {
        if (!context.province.isValid()) {
            return layout;
        }
        if (context.selectIndex != 0) {
            return layout;
        }
        final Set<Province> neighbors = context.province.getNeighbors();
        final List<Province> provinces = new ArrayList<>(1 + neighbors.size());
        // 第一个：当前省份
        provinces.add(context.province);
        // 其它省份，跟第一行几前位进行替换
        provinces.addAll(neighbors);

        final RowEntry firstRow = layout.get(0);
        for (int headIdx = 0; headIdx < provinces.size(); headIdx++) {
            final Province province = provinces.get(headIdx);
            final KeyEntry replaceKey = firstRow.get(headIdx);
            // 找到当前省份简称对应的键位
            SEARCH_TARGET:
            for (RowEntry row : layout) {
                for (int i = 0; i < row.size(); i++) {
                    KeyEntry target = row.get(i);
                    if (province.shortName.equals(target.text)) {
                        // 交换两个省份位置
                        firstRow.set(headIdx, target);
                        row.set(i, replaceKey);
                        break SEARCH_TARGET;
                    }
                }
            }
        }

        return layout;
    }


}
