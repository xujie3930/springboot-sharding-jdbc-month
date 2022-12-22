package com.demo.module.service;

import com.demo.module.entity.TUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ACGkaka
 * @since 2021-04-25
 */
public interface TUserService extends IService<TUser> {

    /**
     * 查询 全部用户（mydb1数据库）
     * @return 全部用户
     */
    List<TUser> listFromDB1();

}
