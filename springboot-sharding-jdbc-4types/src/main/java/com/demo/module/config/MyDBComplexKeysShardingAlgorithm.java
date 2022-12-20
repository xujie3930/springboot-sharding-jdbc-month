package com.demo.module.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p> @Title MyDBComplexKeysShardingAlgorithm
 * <p> @Description 复合分库算法
 *
 * @author zhj
 * @date 2022/10/19 16:55
 */
public class MyDBComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm<Integer> {

    /**
     * 复合分库
     *
     * @param databaseNames 全部库名集合（用于筛选）
     * @param complexKeysShardingValue 复合分片值
     * @return 需要用到的库名集合
     */
    @Override
    public Collection<String> doSharding(Collection<String> databaseNames, ComplexKeysShardingValue<Integer> complexKeysShardingValue) {

        // 精准分片-得到每个分片键对应的值
        Collection<Integer> ageValues = this.getPreciseShardingValue(complexKeysShardingValue, "age");
        Collection<Integer> salaryValues = this.getPreciseShardingValue(complexKeysShardingValue, "salary");

        // 精准分片-根据两个分片键进行分库
        List<String> ageSuffix = ageValues.stream().map(o -> o < 18 ? "1" : "2").collect(Collectors.toList());
        List<String> salarySuffix = salaryValues.stream().map(o -> o < 5000 ? "1" : "2").collect(Collectors.toList());
        List<String> shardingSuffix = getShardingSuffix(ageSuffix, salarySuffix);

        // 范围分片-得到每个分片键对应的值
        Range<Integer> ageRange = this.getRangeShardingValue(complexKeysShardingValue, "age");
        Range<Integer> salaryRange = this.getRangeShardingValue(complexKeysShardingValue, "salary");

        // 范围分片-根据两个分片键进行分库
        ageSuffix = getRangeSuffix(ageRange);
        salarySuffix = getRangeSuffix(salaryRange);
        shardingSuffix.addAll(getShardingSuffix(ageSuffix, salarySuffix));

        return shardingSuffix.stream().distinct().collect(Collectors.toList());
    }


    // --------------------------------------------------------------------------------------------------------------
    // 私有方法
    // --------------------------------------------------------------------------------------------------------------


    /**
     * 获取 分库的范围
     * @param range 分片值范围
     * @return 分库范围
     */
    private List<String> getRangeSuffix(Range<Integer> range) {
        boolean isValid = range != null && (range.hasLowerBound() || range.hasUpperBound());
        if (isValid) {
            List<String> rangeSuffix = new ArrayList<>();
            int lowerEndpoint = range.hasLowerBound() ? range.lowerEndpoint() < 18 ? 1 : 2 : 1;
            int upperEndpoint = range.hasUpperBound() ? range.upperEndpoint() < 18 ? 1 : 2 : 2;
            for (int i = lowerEndpoint; i <= upperEndpoint; i++) {
                rangeSuffix.add(String.valueOf(i));
            }
            return rangeSuffix;
        }
        return new ArrayList<>(0);
    }

    /**
     * 获取 库名集合
     * @param ageSuffix age分片值
     * @param salarySuffix salary分片值
     * @return 库名集合
     */
    private List<String> getShardingSuffix(List<String> ageSuffix, List<String> salarySuffix) {
        List<String> dbNames = new ArrayList<>();
        ageSuffix = ageSuffix == null ? new ArrayList<>(0) : ageSuffix;
        salarySuffix = salarySuffix == null ? new ArrayList<>(0) : salarySuffix;
        for (String age : ageSuffix) {
            for (String salary : salarySuffix) {
                dbNames.add(String.format("mydb-%s-%s", age, salary));
            }
        }
        return dbNames.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取 分片键对应的值
     *
     * @param shardingValue 复合分片值
     * @param key 分片键
     * @return 值
     */
    private Collection<Integer> getPreciseShardingValue(ComplexKeysShardingValue<Integer> shardingValue, final String key) {
        // 判断非空
        if (StringUtils.isEmpty(key) || shardingValue == null) {
            return new ArrayList<>(0);
        }

        // 根据 分片键 取值
        Collection<Integer> valueSet = new ArrayList<>();
        Map<String, Collection<Integer>> columnNameAndShardingValueMap = shardingValue.getColumnNameAndShardingValuesMap();
        Map<String, String> columnNameMap = columnNameAndShardingValueMap.keySet().stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(String::toUpperCase, o -> o, (k1, k2) -> k1));
        if (columnNameMap.containsKey(key.toUpperCase())) {
            String columnName = columnNameMap.get(key.toUpperCase());
            valueSet.addAll(columnNameAndShardingValueMap.get(columnName));
        }
        return valueSet;
    }

    /**
     * 获取 分片键对应的值
     *
     * @param shardingValue 复合分片值
     * @param key 分片键
     * @return 值
     */
    private Range<Integer> getRangeShardingValue(ComplexKeysShardingValue<Integer> shardingValue, final String key) {
        // 判断非空
        if (StringUtils.isEmpty(key) || shardingValue == null) {
            return null;
        }

        // 根据 分片键 取值
        Map<String, Range<Integer>> columnNameAndRangeValuesMap = shardingValue.getColumnNameAndRangeValuesMap();
        Map<String, String> columnNameMap = columnNameAndRangeValuesMap.keySet().stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(String::toUpperCase, o -> o, (k1, k2) -> k1));
        if (columnNameMap.containsKey(key.toUpperCase())) {
            String columnName = columnNameMap.get(key.toUpperCase());
            return columnNameAndRangeValuesMap.get(columnName);
        } else {
            return null;
        }
    }
}
