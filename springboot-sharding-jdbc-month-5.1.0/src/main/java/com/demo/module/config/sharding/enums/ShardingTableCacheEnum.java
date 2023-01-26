package com.demo.module.config.sharding.enums;

import java.util.*;

/**
 * <p> @Title ShardingTableCacheEnum
 * <p> @Description 分片表缓存枚举
 *
 * @author ACGkaka
 * @date 2022/12/23 20:17
 */
public enum ShardingTableCacheEnum {

    /**
     * 用户表
     */
    USER("t_user", new HashSet<>()),
    /**
     * 日志表
     */
    LOG("t_log", new HashSet<>());

    /**
     * 逻辑表名
     */
    private final String logicTableName;
    /**
     * 实际表名
     */
    private final Set<String> resultTableNamesCache;

    private static Map<String, ShardingTableCacheEnum> valueMap = new HashMap<>();

    static {
        Arrays.stream(ShardingTableCacheEnum.values()).forEach(o -> valueMap.put(o.logicTableName, o));
    }

    ShardingTableCacheEnum(String logicTableName, Set<String> resultTableNamesCache) {
        this.logicTableName = logicTableName;
        this.resultTableNamesCache = resultTableNamesCache;
    }

    public static ShardingTableCacheEnum of(String value) {
        return valueMap.get(value);
    }

    public String logicTableName() {
        return logicTableName;
    }

    public Set<String> resultTableNamesCache() {
        return resultTableNamesCache;
    }

    public static Set<String> logicTableNames() {
        return valueMap.keySet();
    }

    @Override
    public String toString() {
        return "ShardingTableCacheEnum{" +
                "logicTableName='" + logicTableName + '\'' +
                ", resultTableNamesCache=" + resultTableNamesCache +
                '}';
    }
}
