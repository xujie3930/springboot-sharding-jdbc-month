package com.demo.module.service.impl;

import com.demo.module.entity.TUser;
import com.demo.module.mapper.TUserMapper;
import com.demo.module.service.TUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements TUserService {

    @Override
    public void addUserA() {
        // 添加用户A
        TUser user = new TUser(null, "username_aaa", "pwd_aaa", LocalDateTime.now(), LocalDateTime.now());
        this.save(user);
    }

    @Override
    public void addUserB() {
        // 添加用户B
        TUser user = new TUser(null, "username_bbb", "pwd_bbb", LocalDateTime.now(), LocalDateTime.now());
        this.save(user);
    }
}
