package com.demo.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p> @Title User
 * <p> @Description 用户实体
 *
 * @author ACGkaka
 * @date 2023/3/20 11:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
