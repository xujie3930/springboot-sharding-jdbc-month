package com.demo.example.service;

import com.demo.example.model.User;

import java.util.List;

/**
 * <p> @Title UserService
 * <p> @Description 用户Service
 *
 * @author ACGkaka
 * @date 2023/3/20 11:06
 */
public interface UserService {

    /**
     * 查询 用户信息列表
     * @return 用户信息列表
     */
    List<User> findList();

}
