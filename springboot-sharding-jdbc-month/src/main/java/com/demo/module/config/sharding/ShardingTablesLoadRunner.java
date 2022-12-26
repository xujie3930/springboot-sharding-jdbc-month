package com.demo.module.config.sharding;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <p> @Title ShardingTablesLoadRunner
 * <p> @Description 项目启动后，读取已有分表，进行缓存
 *
 * @author ACGkaka
 * @date 2022/12/20 15:41
 */
@Order(value = 1) // 数字越小，越先执行
@Component
public class ShardingTablesLoadRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // 读取已有分表，进行缓存
        ShardingAlgorithmTool.tableNameCacheReloadAll();
    }
}
