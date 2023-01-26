package com.demo.module.controller;


import com.demo.module.entity.User;
import com.demo.module.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ACGkaka
 * @since 2021-04-25
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public PageInfo<User> list() {
        PageHelper.startPage(1, 1);
        List<User> list = userService.list();
        return new PageInfo<>(list);
    }
}
