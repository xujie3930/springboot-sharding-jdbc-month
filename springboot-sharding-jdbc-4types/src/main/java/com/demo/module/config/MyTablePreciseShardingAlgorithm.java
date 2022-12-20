package com.demo.module.config;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * <p> @Title MyTablePreciseShardingAlgorithm
 * <p> @Description 精准分表算法
 *
 * @author zhj
 * @date 2022/10/18 15:56
 */
public class MyTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<Integer> {

    @Override
    public String doSharding(Collection<String> tableNames, PreciseShardingValue<Integer> shardingValue) {
        /**
         * tableNames 对应分片库中所有分片表的集合
         * shardingValue 为分片属性，其中 logicTableName 为逻辑表，columnName 分片键，value 为从 SQL 中解析出来的分片键的值
         */
        for (String tableName : tableNames) {
            // 根据年龄判断，未成年 -> t_user_1, 已成年 -> t_user_2
            String value = String.valueOf(shardingValue.getValue() < 18 ? 1 : 2);
            if (tableName.endsWith(value)) {
                return tableName;
            }
        }
        throw new IllegalArgumentException("分片失败，tableNames：" + tableNames);
    }
}
