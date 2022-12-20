package com.demo.module.service;

import com.demo.module.entity.TUser;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * 创建用户表（例：t_user_202201）
     * @param tableName 表名
     */
    void createUserTable(String tableName);

}
