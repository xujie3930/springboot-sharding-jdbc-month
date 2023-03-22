package com.demo.example.controller;

import com.demo.common.Result;
import com.demo.example.model.User;
import com.demo.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p> @Title UserController
 * <p> @Description 用户Controller
 *
 * @author ACGkaka
 * @date 2023/3/20 11:04
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/userList")
    public Result userList() {
        List<User> users = userService.findList();
        return Result.succeed().setData(users);
    }
}
