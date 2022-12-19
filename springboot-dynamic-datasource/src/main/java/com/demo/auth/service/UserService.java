package com.demo.auth.service;

import com.demo.auth.entity.User;

import java.util.List;

/**
 * 用户表(User)表服务接口
 *
 * @author ACGkaka
 * @since 2021-06-18 16:49:55
 */
public interface UserService {

    /**
     * 查询全部数据
     *
     * @return 对象列表
     */
    List<User> findAll();

    /**
     * 查询全部数据
     *
     * @return 对象列表
     */
    List<User> findAll2();

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User insert(User user);

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User insert2(User user);

}