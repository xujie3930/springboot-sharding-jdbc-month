package com.demo.module.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 日志表
 * </p>
 *
 * @author ACGkaka
 * @since 2021-04-25
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("t_log")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 日志
     */
    private String log;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public Log(Long id, String log, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.log = log;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
