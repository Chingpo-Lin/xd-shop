package org.example.service.impl;

import org.example.model.UserDO;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  user service impl
 * </p>
 *
 * @author Bob
 * @since 2022-12-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

}
