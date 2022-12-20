package com.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.module.entity.TUser;
import com.demo.module.service.TUserService;
import org.apache.shardingsphere.api.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringbootDemoApplicationTests {

    @Autowired
    private TUserService userService;

    @Test
    void saveTest() {
        List<TUser> users = new ArrayList<>(3);
        users.add(new TUser("ACGkaka_1", "123456", 10, 3000));
        users.add(new TUser("ACGkaka_2", "123456", 18, 4000));
        users.add(new TUser("ACGkaka_3", "123456", 15, 6000));
        users.add(new TUser("ACGkaka_4", "123456", 19, 7000));
        userService.saveBatch(users);
    }

    @Test
    void listTest() {
        QueryWrapper<TUser> wrapper = new QueryWrapper<>();
        wrapper.ge("age", 18);
        wrapper.ge("salary", 5000);
        List<TUser> users = userService.list(wrapper);
        System.out.println(">>>>>>>>>> 【Result】 <<<<<<<<<< ");
        users.forEach(System.out::println);
    }

    @Test
    void hintSaveTest() {
        // 清除掉上一次的规则，否则会报错
        HintManager.clear();
        // HintManager API 工具类实例
        HintManager hintManager = HintManager.getInstance();
        // 直接指定对应具体的数据库
        hintManager.addDatabaseShardingValue("mydb",0);
        // 设置表的分片键值，自定义操作哪个分片中
        hintManager.addTableShardingValue("t_user" , 18);

        // 在读写分离数据库中，Hint 可以强制读主库
        hintManager.setMasterRouteOnly();

        List<TUser> users = new ArrayList<>(3);
        users.add(new TUser("ACGkaka_1", "123456", 10, 3000));
        users.add(new TUser("ACGkaka_2", "123456", 18, 4000));
        users.add(new TUser("ACGkaka_3", "123456", 15, 6000));
        users.add(new TUser("ACGkaka_4", "123456", 19, 7000));
        userService.saveBatch(users);
    }

    @Test
    void hintListTest() {
        // 清除掉上一次的规则，否则会报错
        HintManager.clear();
        // HintManager API 工具类实例
        HintManager hintManager = HintManager.getInstance();
        // 直接指定对应具体的数据库
        hintManager.addDatabaseShardingValue("mydb",0);
        // 设置表的分片键值，自定义操作哪个分片中
        hintManager.addTableShardingValue("t_user" , 18);

        // 在读写分离数据库中，Hint 可以强制读主库
        hintManager.setMasterRouteOnly();

        QueryWrapper<TUser> wrapper = new QueryWrapper<>();
        List<TUser> users = userService.list(wrapper);
        System.out.println(">>>>>>>>>> 【Result】 <<<<<<<<<< ");
        users.forEach(System.out::println);
    }

}
