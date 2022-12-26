package com.demo.module.controller;


import com.demo.module.entity.User;
import com.demo.module.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 日志表 前端控制器
 * </p>
 *
 * @author ACGkaka
 * @since 2021-04-25
 */
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }
}
