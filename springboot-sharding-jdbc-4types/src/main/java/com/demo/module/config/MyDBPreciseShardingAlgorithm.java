package com.demo.module.config;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * <p> @Title MyDBPreciseShardingAlgorithm
 * <p> @Description 精准分库算法
 *
 * @author zhj
 * @date 2022/10/19 11:01
 */
public class MyDBPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Integer> {

    @Override
    public String doSharding(Collection<String> databaseNames, PreciseShardingValue<Integer> shardingValue) {

        /**
         * databaseNames 所有分片库的集合
         * shardingValue 为分片属性，其中 logicTableName 为逻辑表，columnName 分片键，value 为从 SQL 中解析出的分片键的值
         */
        for (String databaseName : databaseNames) {
            // 根据年龄判断，未成年 -> mydb-1, 已成年 -> mydb-2
            String value = String.valueOf(shardingValue.getValue() < 18 ? 1 : 2);
            if (databaseName.endsWith(value)) {
                return databaseName;
            }
        }
        throw new IllegalArgumentException("分片失败，databaseNames：" + databaseNames);
    }
}
