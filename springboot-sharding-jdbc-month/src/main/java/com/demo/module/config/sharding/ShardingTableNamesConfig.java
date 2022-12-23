package com.demo.module.config.sharding;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.core.rule.DataNode;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p> @Title ShardingTableNamesConfig
 * <p> @Description 分片表名配置
 *
 * @author zhj
 * @date 2022/12/23 16:29
 */
@Slf4j
@Component
public class ShardingTableNamesConfig {

    @Autowired
    private DataSource dataSource;

    /**
     *  动态更新配置 actualDataNodes
     */
    public void actualDataNodesRefresh()  {
        try {
            ShardingDataSource dataSource = (ShardingDataSource) this.dataSource;
            Set<String> tableNameCache = ShardingAlgorithmTool.getTableNameCache();
            log.info(">>>>>>>>>> 【INFO】刷新分表缓存，tableNamesCache: {}", tableNameCache);

            // 获取数据分片节点
            TableRule tableRule = dataSource.getShardingContext().getShardingRule().getTableRule(ShardingAlgorithmTool.LOGIC_TABLE_NAME);
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
}
