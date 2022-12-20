package com.demo.module.config;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p> @Title MyTableHintShardingAlgorithm
 * <p> @Description Hint分表算法
 *
 * @author zhj
 * @date 2022/10/21 12:25
 */
public class MyTableHintShardingAlgorithm implements HintShardingAlgorithm<Integer> {

    @Override
    public Collection<String> doSharding(Collection<String> tableNames, HintShardingValue<Integer> hintShardingValue) {

        List<String> result = new ArrayList<>();
        for (Integer shardingValue : hintShardingValue.getValues()) {
            result.add(shardingValue < 18 ? "t_user_1" : "t_user_2");
        }
        return result;
    }
}
