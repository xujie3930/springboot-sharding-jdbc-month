package com.demo;

import com.demo.module.entity.User;
import com.demo.module.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    private UserService userService;

    @Test
    void saveTest() {
        List<User> user1s = new ArrayList<>(3);
        LocalDateTime time1 = LocalDateTime.parse("2022-01-01 00:00:00", DATE_TIME_FORMATTER);
        LocalDateTime time2 = LocalDateTime.parse("2022-02-01 00:00:00", DATE_TIME_FORMATTER);
        user1s.add(new User("ACGkaka_1", "123456", 10, time1, time1));
        user1s.add(new User("ACGkaka_2", "123456", 11, time2, time2));
        userService.saveBatch(user1s);
    }

    @Test
    void listTest() {
        PageHelper.startPage(1, 1);
        List<User> user1s = userService.list();
        PageInfo<User> pageInfo = new PageInfo<>(user1s);
        System.out.println(">>>>>>>>>> 【Result】<<<<<<<<<< ");
        System.out.println(pageInfo);
    }
}
