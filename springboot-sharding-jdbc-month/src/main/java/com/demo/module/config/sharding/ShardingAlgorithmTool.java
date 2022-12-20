package com.demo.module.config.sharding;

import com.alibaba.druid.util.StringUtils;
import com.demo.module.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.sql.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p> @Title ShardingAlgorithmTool
 * <p> @Description 按月分片算法工具
 *
 * @author zhj
 * @date 2022/12/20 14:03
 */
@Slf4j
public class ShardingAlgorithmTool {

    /** 已存在表名集合缓存 */
    private static final Set<String> tableNameCache = new HashSet<>();
    /** 用户表名 */
    private static final String userTableName = "t_user";
    /** 表分片符号，例：t_user_202201 中，分片符号为 "_" */
    private static final String tableSplitSymbol = "_";
    /** 数据库配置 */
    private static final Environment env = SpringUtil.getApplicationContext().getEnvironment();
    private static final String url = env.getProperty("spring.shardingsphere.datasource.mydb.url");
    private static final String username = env.getProperty("spring.shardingsphere.datasource.mydb.username");
    private static final String password = env.getProperty("spring.shardingsphere.datasource.mydb.password");

    /**
     * 检查分表获取的表名是否存在，不存在则自动建表
     * @param logicTableName 逻辑表名，例：t_user
     * @param resultTableNames 真实表名，例：t_user_202201
     * @return 存在于数据库中的真实表名集合
     */
    public static Set<String> getShardingTablesAndCreate(String logicTableName, Collection<String> resultTableNames) {
        return resultTableNames.stream().map(o -> getShardingTableAndCreate(logicTableName, o)).collect(Collectors.toSet());
    }

    /**
     * 检查分表获取的表名是否存在，不存在则自动建表
     * @param logicTableName 逻辑表名，例：t_user
     * @param resultTableName 真实表名，例：t_user_202201
     * @return 确认存在于数据库中的真实表名
     */
    public static String getShardingTableAndCreate(String logicTableName, String resultTableName) {
        // 缓存中有此表则返回，没有则判断创建
        if (tableNameCache.contains(resultTableName)) {
            return resultTableName;
        } else {
            // 未创建的表返回逻辑空表
            boolean isSuccess = createShardingTable(logicTableName, resultTableName);
            return isSuccess ? resultTableName : logicTableName;
        }
    }

    /**
     * 缓存重载
     */
    public static void tableNameCacheReload() {
        // 读取数据库中|所有表名
        List<String> tableNameList = getAllTableNameBySchema();
        // 删除旧的缓存（如果存在）
        tableNameCache.clear();
        // 写入新的缓存
        ShardingAlgorithmTool.tableNameCache.addAll(tableNameList);
    }

    /**
     * 获取所有表名
     * @return 表名集合
     */
    public static List<String> getAllTableNameBySchema() {
        List<String> tableNames = new ArrayList<>();
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，url:{}, username:{}, password:{}", url, username, password);
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement st = conn.createStatement()) {
            try (ResultSet rs = st.executeQuery("show TABLES like 't_user_%'")) {
                while (rs.next()) {
                    tableNames.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接失败，请稍后重试，原因：{}", e.getMessage(), e);
            throw new IllegalArgumentException("数据库连接失败，请稍后重试");
        }
        return tableNames;
    }

    /**
     * 获取表名缓存
     * @return 表名缓存
     */
    public static Set<String> getTableNameCache() {
        return tableNameCache;
    }


    // --------------------------------------------------------------------------------------------------------------
    // 私有方法
    // --------------------------------------------------------------------------------------------------------------


    /**
     * 创建分表
     * @param logicTableName 逻辑表名，例：t_user
     * @param resultTableName 真实表名，例：t_user_202201
     * @return 创建结果（true创建成功，false未创建）
     */
    private static boolean createShardingTable(String logicTableName, String resultTableName) {
        // 根据日期判断，当前月份之后月份分表不提前创建
        String month = resultTableName.replace(logicTableName + tableSplitSymbol,"");
        YearMonth shardingMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"));
        if (shardingMonth.isAfter(YearMonth.now())) {
            return false;
        }

        synchronized (logicTableName.intern()) {
            // 缓存中有此表 返回
            if (tableNameCache.contains(resultTableName)) {
                return false;
            }
            // 缓存中无此表，则建表并添加缓存
            List<String> sqlList = getCreateTableSql(logicTableName);
            for (int i = 0; i < sqlList.size(); i++) {
                sqlList.set(i, sqlList.get(i).replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS").replace(logicTableName, resultTableName));
            }
            executeSql(sqlList);
            tableNameCache.add(resultTableName);
        }
        return true;
    }

    /**
     * 执行SQL
     * @param sqlList SQL集合
     */
    private static void executeSql(List<String> sqlList) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，url:{}, username:{}, password:{}", url, username, password);
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            try (Statement st = conn.createStatement()) {
                conn.setAutoCommit(false);
                for (String sql : sqlList) {
                    st.execute(sql);
                }
            } catch (Exception e) {
                conn.rollback();
                log.error(">>>>>>>>>> 【ERROR】数据表创建执行失败，请稍后重试，原因：{}", e.getMessage(), e);
                throw new IllegalArgumentException("数据表创建执行失败，请稍后重试");
            }
        } catch (SQLException e) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接失败，请稍后重试，原因：{}", e.getMessage(), e);
            throw new IllegalArgumentException("数据库连接失败，请稍后重试");
        }
    }

    /**
     * 获取建表语句
     * @param tableName 表名，例：t_user
     * @return 建表语句集合
     */
    private static List<String> getCreateTableSql(String tableName) {
        List<String> sqlList = new ArrayList<>();
        if (tableName.equals(userTableName)) {
            // 表结构
            sqlList.add("CREATE TABLE `t_user` (\n" +
                    "  `id` bigint(16) NOT NULL COMMENT '主键',\n" +
                    "  `username` varchar(64) NOT NULL COMMENT '用户名',\n" +
                    "  `password` varchar(64) NOT NULL COMMENT '密码',\n" +
                    "  `age` int(8) NOT NULL COMMENT '年龄',\n" +
                    "  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                    "  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表202203';");
            // 表索引
            sqlList.add("ALTER TABLE `t_user` ADD INDEX IDX_USERNAME ( `username` ) USING BTREE");
        }
        return sqlList;
    }

}
