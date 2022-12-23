package com.demo.module.config.sharding;

import com.alibaba.druid.util.StringUtils;
import com.demo.module.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.core.rule.DataNode;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    /** 逻辑表名，例：t_user */
    public static final String LOGIC_TABLE_NAME = "t_user";
    /** 表分片符号，例：t_user_202201 中，分片符号为 "_" */
    private static final String TABLE_SPLIT_SYMBOL = "_";

    /** 已存在表名集合缓存 */
    private static final Set<String> TABLE_NAME_CACHE = new HashSet<>();

    /** 数据库配置 */
    private static final Environment ENV = SpringUtil.getApplicationContext().getEnvironment();
    private static final String DATASOURCE_URL = ENV.getProperty("spring.shardingsphere.datasource.mydb.url");
    private static final String DATASOURCE_USERNAME = ENV.getProperty("spring.shardingsphere.datasource.mydb.username");
    private static final String DATASOURCE_PASSWORD = ENV.getProperty("spring.shardingsphere.datasource.mydb.password");


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
        if (TABLE_NAME_CACHE.contains(resultTableName)) {
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
        TABLE_NAME_CACHE.clear();
        // 写入新的缓存
        TABLE_NAME_CACHE.addAll(tableNameList);
    }

    /**
     * 获取所有表名
     * @return 表名集合
     */
    public static List<String> getAllTableNameBySchema() {
        List<String> tableNames = new ArrayList<>();
        if (StringUtils.isEmpty(DATASOURCE_URL) || StringUtils.isEmpty(DATASOURCE_USERNAME) || StringUtils.isEmpty(DATASOURCE_PASSWORD)) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，URL:{}, username:{}, password:{}", DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD);
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
        try (Connection conn = DriverManager.getConnection(DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD);
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
        return TABLE_NAME_CACHE;
    }

    /**
     *  动态更新配置 actualDataNodes
     */
    public static void actualDataNodesRefresh()  {
        try {
            // 获取数据分片节点
            Set<String> tableNameCache = ShardingAlgorithmTool.getTableNameCache();
            ShardingDataSource dataSource = (ShardingDataSource) SpringUtil.getBean("dataSource", DataSource.class);
            TableRule tableRule = dataSource.getShardingContext().getShardingRule().getTableRule(LOGIC_TABLE_NAME);
            List<DataNode> dataNodes = tableRule.getActualDataNodes();
            String dataSourceName = dataNodes.get(0).getDataSourceName();
            List<DataNode> newDataNodes = tableNameCache.stream().map(tableName -> new DataNode(dataSourceName + "." + tableName)).collect(Collectors.toList());

            // 更新actualDataNodes
            Field actualDataNodesField = TableRule.class.getDeclaredField("actualDataNodes");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(actualDataNodesField, actualDataNodesField.getModifiers() & ~Modifier.FINAL);
            actualDataNodesField.setAccessible(true);
            actualDataNodesField.set(tableRule, newDataNodes);
        }catch (Exception e){
            log.error("初始化 动态表单失败，原因：{}", e.getMessage(), e);
        }
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
        // 根据日期判断，当前月份之后分表不提前创建
        String month = resultTableName.replace(logicTableName + TABLE_SPLIT_SYMBOL,"");
        YearMonth shardingMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"));
        if (shardingMonth.isAfter(YearMonth.now())) {
            return false;
        }

        synchronized (logicTableName.intern()) {
            // 缓存中有此表 返回
            if (TABLE_NAME_CACHE.contains(resultTableName)) {
                return false;
            }
            // 缓存中无此表，则建表并添加缓存
            executeSql(Collections.singletonList("CREATE TABLE IF NOT EXISTS `" + resultTableName + "` LIKE `" + logicTableName + "`;"));
            // 缓存重载
            tableNameCacheReload();
            // 动态更新配置 actualDataNodes
            ShardingTableNamesConfig shardingTableNamesConfig = SpringUtil.getBean("shardingTableNamesConfig", ShardingTableNamesConfig.class);
            shardingTableNamesConfig.actualDataNodesRefresh();
        }
        return true;
    }

    /**
     * 执行SQL
     * @param sqlList SQL集合
     */
    private static void executeSql(List<String> sqlList) {
        if (StringUtils.isEmpty(DATASOURCE_URL) || StringUtils.isEmpty(DATASOURCE_USERNAME) || StringUtils.isEmpty(DATASOURCE_PASSWORD)) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，URL:{}, username:{}, password:{}", DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD);
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
        try (Connection conn = DriverManager.getConnection(DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD)) {
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

}
