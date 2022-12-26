package com.demo.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.module.entity.Log;
import com.demo.module.mapper.LogMapper;
import com.demo.module.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 日志表 服务实现类
 * </p>
 *
 * @author ACGkaka
 * @since 2021-04-25
 */
@Slf4j
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

}
