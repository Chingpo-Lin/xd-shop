package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.enums.BizCodeEnum;
import org.example.enums.SendCodeEnum;
import org.example.mapper.UserMapper;
import org.example.model.UserDO;
import org.example.request.UserRegisterRequest;
import org.example.service.NotifyService;
import org.example.service.UserService;
import org.example.utils.JsonData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private UserMapper userMapper;

    /**
     * user register
     * * code confirmation
     * * encryption
     * * check unique account
     * * insert db
     * * new user benefits
     *
     * @param registerRequest
     * @return
     */
    @Override
    public JsonData register(UserRegisterRequest registerRequest) {

        boolean checkCode = false;
        if (!StringUtils.isEmpty(registerRequest.getMail())) {
            checkCode = notifyService.checkCode(SendCodeEnum.USER_REGISTER, registerRequest.getMail(), registerRequest.getCode());
        }
            
        if (!checkCode) {
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }

        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(registerRequest, userDO);

        userDO.setCreateTime(new Date());
        userDO.setSlogan("Hello, I am " + userDO.getName());
        // set password TODO

        // check if email exist TODO
        if (checkUnique(userDO.getMail())) {
            // store into db
            int rows = userMapper.insert(userDO);
            log.info("rows:{}, register success:{}", rows, userDO.toString());

            // new user benefit TODO
            userRegisterInitTask(userDO);
            return JsonData.buildSuccess();
        } else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_REPEAT);
        }
    }

    /**
     * check if account is unique
     * @param mail
     * @return
     */
    private boolean checkUnique(String mail) {
        return false;
    }

    /**
     * initilize new user info
     * @param userDO
     */
    private void userRegisterInitTask(UserDO userDO) {

    }
}
