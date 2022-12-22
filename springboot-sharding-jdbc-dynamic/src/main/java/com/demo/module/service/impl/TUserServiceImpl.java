package com.demo.module.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.demo.module.config.DataSourceConfig;
import com.demo.module.entity.TUser;
import com.demo.module.mapper.TUserMapper;
import com.demo.module.service.TUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ACGkaka
 * @since 2021-04-25
 */
@Service
@DS(DataSourceConfig.SHARDING_DATA_SOURCE_NAME)
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements TUserService {

    @DS("mydb1")
    @Override
    public List<TUser> listFromDB1() {
        // 查询 全部用户（mydb1数据库）
        return this.list();
    }
}
