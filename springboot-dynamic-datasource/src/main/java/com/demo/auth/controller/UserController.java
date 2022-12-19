package com.demo.auth.controller;

import com.demo.auth.entity.User;
import com.demo.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户表(User)表控制层
 *
 * @author ACGkaka
 * @since 2021-06-18 16:49:55
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 插入单条数据
     *
     * @return 单条数据
     */
    @GetMapping("/insertOne")
    public User insertOne() {
        User user = new User();
        user.setUsername("ACGkaka1");
        user.setPassword("123456");
        user.setEmail("123@123.com");
        user.setPhone("15588888888");
        user.setDelFlag("0");
        return userService.insert(user);
    }

    /**
     * 插入单条数据
     *
     * @return 单条数据
     */
    @GetMapping("/insertOne2")
    public User insertOne2() {
        User user = new User();
        user.setUsername("ACGkaka2");
        user.setPassword("123456");
        user.setEmail("123@123.com");
        user.setPhone("15588888888");
        user.setDelFlag("0");
        return userService.insert2(user);
    }

    /**
     * 查询全部数据
     *
     * @return 全部数据
     */
    @GetMapping("/findAll")
    public List<User> findAll() {
        return userService.findAll();
    }

    /**
     * 查询全部数据
     *
     * @return 全部数据
     */
    @GetMapping("/findAll2")
    public List<User> findAll2() {
        return userService.findAll2();
    }

}