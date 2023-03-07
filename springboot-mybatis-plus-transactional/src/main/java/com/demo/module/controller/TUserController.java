package com.demo.module.controller;


import com.demo.module.common.Result;
import com.demo.module.entity.TUser;
import com.demo.module.service.TUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class TUserController {

    @Autowired
    private TUserService tUserService;

    @GetMapping("/addUserA")
    public Result addUserA() {
        tUserService.addUserA();
        return Result.succeed();
    }

    @GetMapping("/addUserB")
    public Result addUserB() {
        tUserService.addUserB();
        return Result.succeed();
    }

}
