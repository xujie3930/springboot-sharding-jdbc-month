package com.demo.module.config.sharding;

import com.alibaba.druid.util.StringUtils;
import com.demo.module.config.sharding.enums.ShardingTableCacheEnum;
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
 * @author ACGkaka
 * @date 2022/12/20 14:03
 */
@Slf4j
public class ShardingAlgorithmTool {

    /** 表分片符号，例：t_contract_202201 中，分片符号为 "_" */
    private static final String TABLE_SPLIT_SYMBOL = "_";

    /** 数据库配置 */
    private static final Environment ENV = SpringUtil.getApplicationContext().getEnvironment();
    private static final String DATASOURCE_URL = ENV.getProperty("spring.shardingsphere.datasource.mydb.url");
    private static final String DATASOURCE_USERNAME = ENV.getProperty("spring.shardingsphere.datasource.mydb.username");
    private static final String DATASOURCE_PASSWORD = ENV.getProperty("spring.shardingsphere.datasource.mydb.password");


    /**
     * 检查分表获取的表名是否存在，不存在则自动建表
     * @param logicTable 逻辑表
     * @param resultTableNames 真实表名，例：t_contract_202201
     * @return 存在于数据库中的真实表名集合
     */
    public static Set<String> getShardingTablesAndCreate(ShardingTableCacheEnum logicTable, Collection<String> resultTableNames) {
        return resultTableNames.stream().map(o -> getShardingTableAndCreate(logicTable, o)).collect(Collectors.toSet());
    }

    /**
     * 检查分表获取的表名是否存在，不存在则自动建表
     * @param logicTable 逻辑表
     * @param resultTableName 真实表名，例：t_contract_202201
     * @return 确认存在于数据库中的真实表名
     */
    public static String getShardingTableAndCreate(ShardingTableCacheEnum logicTable, String resultTableName) {
        // 缓存中有此表则返回，没有则判断创建
        if (logicTable.resultTableNamesCache().contains(resultTableName)) {
            return resultTableName;
        } else {
            // 未创建的表返回逻辑空表
            boolean isSuccess = createShardingTable(logicTable, resultTableName);
            return isSuccess ? resultTableName : logicTable.logicTableName();
        }
    }

    /**
     * 重载全部缓存
     */
    public static void tableNameCacheReloadAll() {
        Arrays.stream(ShardingTableCacheEnum.values()).forEach(ShardingAlgorithmTool::tableNameCacheReload);
    }

    /**
     * 重载指定分表缓存
     * @param logicTable 逻辑表，例：t_contract
     */
    public static void tableNameCacheReload(ShardingTableCacheEnum logicTable) {
        // 读取数据库中|所有表名
        List<String> tableNameList = getAllTableNameBySchema(logicTable);
        // 删除旧的缓存（如果存在）
        logicTable.resultTableNamesCache().clear();
        // 写入新的缓存
        logicTable.resultTableNamesCache().addAll(tableNameList);
        // 动态更新配置 actualDataNodes
        actualDataNodesRefresh(logicTable);
    }

    /**
     * 获取所有表名
     * @return 表名集合
     * @param logicTable 逻辑表
     */
    public static List<String> getAllTableNameBySchema(ShardingTableCacheEnum logicTable) {
        List<String> tableNames = new ArrayList<>();
        if (StringUtils.isEmpty(DATASOURCE_URL) || StringUtils.isEmpty(DATASOURCE_USERNAME) || StringUtils.isEmpty(DATASOURCE_PASSWORD)) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，URL:{}, username:{}, password:{}", DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD);
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
        try (Connection conn = DriverManager.getConnection(DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD);
             Statement st = conn.createStatement()) {
            String logicTableName = logicTable.logicTableName();
            try (ResultSet rs = st.executeQuery("show TABLES like '" + logicTableName + TABLE_SPLIT_SYMBOL + "%'")) {
                while (rs.next()) {
                    String tableName = rs.getString(1);
                    // 匹配分表格式 例：^(t\_contract_\d{6})$
                    if (tableName != null && tableName.matches(String.format("^(%s\\d{6})$", logicTableName + TABLE_SPLIT_SYMBOL))) {
                        tableNames.add(rs.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接失败，请稍后重试，原因：{}", e.getMessage(), e);
            throw new IllegalArgumentException("数据库连接失败，请稍后重试");
        }
        return tableNames;
    }

    /**
     *  动态更新配置 actualDataNodes
     * @param logicTable
     */
    public static void actualDataNodesRefresh(ShardingTableCacheEnum logicTable)  {
        try {
            // 获取数据分片节点
            String logicTableName = logicTable.logicTableName();
            Set<String> tableNamesCache = logicTable.resultTableNamesCache();
            log.info(">>>>>>>>>> 【INFO】更新分表配置，logicTableName:{}，tableNamesCache:{}", logicTableName, tableNamesCache);
            ShardingDataSource dataSource = (ShardingDataSource) SpringUtil.getBean("dataSource", DataSource.class);
            TableRule tableRule = dataSource.getShardingContext().getShardingRule().getTableRule(logicTableName);
            List<DataNode> dataNodes = tableRule.getActualDataNodes();
            String dataSourceName = dataNodes.get(0).getDataSourceName();
            List<DataNode> newDataNodes = tableNamesCache.stream().map(tableName -> new DataNode(dataSourceName + "." + tableName)).collect(Collectors.toList());

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
     * @param logicTable 逻辑表
     * @param resultTableName 真实表名，例：t_contract_202201
     * @return 创建结果（true创建成功，false未创建）
     */
    private static boolean createShardingTable(ShardingTableCacheEnum logicTable, String resultTableName) {
        // 根据日期判断，当前月份之后分表不提前创建
        String month = resultTableName.replace(logicTable.logicTableName() + TABLE_SPLIT_SYMBOL,"");
        YearMonth shardingMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"));
        if (shardingMonth.isAfter(YearMonth.now())) {
            return false;
        }

        synchronized (logicTable.logicTableName().intern()) {
            // 缓存中有此表 返回
            if (logicTable.resultTableNamesCache().contains(resultTableName)) {
                return false;
            }
            // 缓存中无此表，则建表并添加缓存
            executeSql(Collections.singletonList("CREATE TABLE IF NOT EXISTS `" + resultTableName + "` LIKE `" + logicTable.logicTableName() + "`;"));
            // 缓存重载
            tableNameCacheReload(logicTable);
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
