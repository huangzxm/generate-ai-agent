package com.yupi.generateaiagent.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yupi.generateaiagent.entity.App;
import com.yupi.generateaiagent.mapper.AppMapper;
import com.yupi.generateaiagent.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/huangzxm">huangzxm</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
