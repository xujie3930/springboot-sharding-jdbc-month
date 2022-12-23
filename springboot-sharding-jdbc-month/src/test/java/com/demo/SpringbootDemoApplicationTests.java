package com.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.module.entity.TUser;
import com.demo.module.service.TUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringbootDemoApplicationTests {

    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TUserService userService;

    @Test
    void saveTest() {
        List<TUser> users = new ArrayList<>(3);
        LocalDateTime time1 = LocalDateTime.parse("2022-01-01 00:00:00", DATE_TIME_FORMATTER);
        LocalDateTime time2 = LocalDateTime.parse("2022-02-01 00:00:00", DATE_TIME_FORMATTER);
        LocalDateTime time3 = LocalDateTime.parse("2022-03-01 00:00:00", DATE_TIME_FORMATTER);
        users.add(new TUser("ACGkaka_1", "123456", 10, time1, time1));
//        users.add(new TUser("ACGkaka_2", "123456", 11, time2, time2));
//        users.add(new TUser("ACGkaka_3", "123456", 12, time3, time3));
        userService.saveBatch(users);
    }

    @Test
    void listTest() {
        LocalDateTime timeStart1 = LocalDateTime.parse("2022-01-01 00:00:00", DATE_TIME_FORMATTER);
        LocalDateTime timeEnd1 = LocalDateTime.parse("2022-01-31 23:59:59", DATE_TIME_FORMATTER);
        List<TUser> users = userService.list(new QueryWrapper<TUser>().between("create_time", timeStart1, timeEnd1));
        System.out.println(">>>>>>>>>> 【Result】<<<<<<<<<< ");
        users.forEach(System.out::println);
    }

    @Test
    void findByIdTest() {
        TUser user = userService.getById(1606125633996324865L);
        System.out.println(">>>>>>>>>> 【Result】<<<<<<<<<< ");
        System.out.println(user);
    }

}
