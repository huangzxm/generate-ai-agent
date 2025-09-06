package com.yupi.yuaicodemother.genresult.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yupi.yuaicodemother.genresult.entity.User;
import com.yupi.yuaicodemother.genresult.mapper.UserMapper;
import com.yupi.yuaicodemother.genresult.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/huangzxm">huangzxm</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

}
