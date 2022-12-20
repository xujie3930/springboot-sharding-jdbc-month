package com.demo.module.config;

import com.google.common.collect.Range;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p> @Title MyDBRangeShardingAlgorithm
 * <p> @Description 范围分库算法
 *
 * @author zhj
 * @date 2022/10/19 13:40
 */
public class MyDBRangeShardingAlgorithm implements RangeShardingAlgorithm<Integer> {

    /**
     * 数据库分片符号
     */
    private final String DATABASE_SPLIT_SYMBOL = "-";

    @Override
    public Collection<String> doSharding(Collection<String> databaseNames, RangeShardingValue<Integer> rangeShardingValue) {
        Set<String> result = new LinkedHashSet<>();

        // between and 的起始值
        Range<Integer> valueRange = rangeShardingValue.getValueRange();
        boolean hasLowerBound = valueRange.hasLowerBound();
        boolean hasUpperBound = valueRange.hasUpperBound();

        // 计算最大值和最小值（未成年 -> mydb-1, 已成年 -> mydb-2）
        int lower;
        if (hasLowerBound) {
            lower = valueRange.lowerEndpoint() < 18 ? 1 : 2;
        } else {
            lower = getLowerEndpoint(databaseNames);
        }
        int upper;
        if (hasUpperBound) {
            upper = valueRange.upperEndpoint() < 18 ? 1 : 2;
        } else {
            upper = getUpperEndpoint(databaseNames);
        }

        // 循环范围计算分库逻辑
        for (int i = lower; i <= upper; i++) {
            for (String databaseName : databaseNames) {
                String value = String.valueOf(i);
                if (databaseName.endsWith(value)) {
                    result.add(databaseName);
                }
            }
        }
        return result;
    }

    // --------------------------------------------------------------------------------------------------------------
    // 私有方法
    // --------------------------------------------------------------------------------------------------------------

    /**
     * 获取 最小分片值
     * @param databaseNames 数据库名
     * @return 最小分片值
     */
    private int getLowerEndpoint(Collection<String> databaseNames) {
        if (CollectionUtils.isNotEmpty(databaseNames)) {
            return databaseNames.stream().filter(o -> o != null && o.contains(DATABASE_SPLIT_SYMBOL))
                    .mapToInt(o -> {
                        String[] splits = o.split(DATABASE_SPLIT_SYMBOL);
                        return Integer.valueOf(splits[splits.length - 1]);
                    }).min().orElse(-1);
        }
        return -1;
    }

    /**
     * 获取 最大分片值
     * @param databaseNames 数据库名
     * @return 最大分片值
     */
    private int getUpperEndpoint(Collection<String> databaseNames) {
        if (CollectionUtils.isNotEmpty(databaseNames)) {
            return databaseNames.stream().filter(o -> o != null && o.contains(DATABASE_SPLIT_SYMBOL))
                    .mapToInt(o -> {
                        String[] splits = o.split(DATABASE_SPLIT_SYMBOL);
                        return Integer.valueOf(splits[splits.length - 1]);
                    }).max().orElse(-1);
        }
        return -1;
    }
}
