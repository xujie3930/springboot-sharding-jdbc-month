package com.demo.module.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.demo.module.entity.TUser;
import com.demo.module.mapper.TUserMapper;
import com.demo.module.service.TUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ACGkaka
 * @since 2021-04-25
 */
@Slf4j
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements TUserService {

    @Autowired
    private TUserMapper userMapper;

    @Override
    public void createUserTable(String tableName) {
        // 创建用户表（例：t_user_202201）
        if (tableName != null && tableName.trim().length() > 0) {
            if (tableName.startsWith("t_user_") && tableName.length() == "t_user_202201".length()) {
                userMapper.createUserTable(tableName);
            }
            log.error(">>>>>>>>>> 【ERROR】用户表创建失败，表名格式异常，表名：{}", tableName);
        }
    }
}
