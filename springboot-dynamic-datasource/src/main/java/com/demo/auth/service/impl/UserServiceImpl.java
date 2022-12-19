package com.demo.auth.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.demo.auth.entity.User;
import com.demo.auth.mapper.UserMapper;
import com.demo.auth.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户表(User)表服务实现类
 *
 * @author ACGkaka
 * @since 2021-06-18 16:49:55
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> findAll() {
        // 查询多条数据
        return this.userMapper.queryAllByLimit(1, -1);
    }

    @DS("mydb1")
    @Override
    public List<User> findAll2() {
        // 查询多条数据
        return this.userMapper.queryAllByLimit(1, -1);
    }

    @Override
    public User insert(User user) {
        // 新增数据
        this.userMapper.insert(user);
        return user;
    }

    @DS("mydb1")
    @Override
    public User insert2(User user) {
        // 新增数据
        this.userMapper.insert(user);
        return user;
    }
}