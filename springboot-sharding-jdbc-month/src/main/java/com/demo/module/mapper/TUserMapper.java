package com.demo.module.mapper;

import com.demo.module.entity.TUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ACGkaka
 * @since 2021-04-25
 */
@Mapper
public interface TUserMapper extends BaseMapper<TUser> {

    /**
     * 创建用户表（例：t_user_202201）
     *
     * @param tableName 表名
     */
    void createUserTable(@Param("tableName") String tableName);
}
