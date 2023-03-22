package com.demo.example.service.impl;

import com.demo.example.model.User;
import com.demo.example.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * <p> @Title UserServiceImpl
 * <p> @Description 用户ServiceImpl
 *
 * @author ACGkaka
 * @date 2023/3/20 11:07
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public List<User> findList() {
        // 查询 用户列表
        User user = new User("ACGkaka", "pwd_111", LocalDateTime.now(), LocalDateTime.now());
        return Collections.singletonList(user);
    }
}