package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.example.enums.BizCodeEnum;
import org.example.enums.SendCodeEnum;
import org.example.feign.CouponFeignService;
import org.example.interceptor.LoginInterceptor;
import org.example.mapper.UserMapper;
import org.example.model.LoginUser;
import org.example.model.UserDO;
import org.example.request.NewUserCouponRequest;
import org.example.request.UserLoginRequest;
import org.example.request.UserRegisterRequest;
import org.example.service.NotifyService;
import org.example.service.UserService;
import org.example.utils.CommonUtil;
import org.example.utils.JWTUtil;
import org.example.utils.JsonData;
import org.example.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private StringRedisTemplate redisTemplate;

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
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    // generally do not use it here since if coupon error, we don't want to rollback register
//    @GlobalTransactional
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
        // set password
        // generate password with MD5 + secret
        userDO.setSecret("$1$" + CommonUtil.getStringNumRandom(8));

        String cryptPwd = Md5Crypt.md5Crypt(registerRequest.getPwd().getBytes(), userDO.getSecret());
        userDO.setPwd(cryptPwd);

        // check if email exist
        if (checkUnique(userDO.getMail())) {
            // store into db
            int rows = userMapper.insert(userDO);
            log.info("rows:{}, register success:{}", rows, userDO.toString());

            // new user benefit
            userRegisterInitTask(userDO);
            return JsonData.buildSuccess();
        } else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_REPEAT);
        }
    }

    /**
     * 1. check in db if mail exists
     * 2. if yes, use pwd + secret to encode and compare with db
     * @param userLoginRequest
     * @return
     */
    @Override
    public JsonData login(UserLoginRequest userLoginRequest) {

        List<UserDO> userDOList = userMapper.selectList(
                new QueryWrapper<UserDO>().eq("mail", userLoginRequest.getMail()));

        if (userDOList != null && userDOList.size() > 0) {
            // exist
            UserDO userDO = userDOList.get(0);
            String cryptPwd = Md5Crypt.md5Crypt(userLoginRequest.getPwd().getBytes(), userDO.getSecret());
            if (cryptPwd.equals(userDO.getPwd())) {
                // login success, generate token
                LoginUser loginUser = LoginUser.builder().build();
                BeanUtils.copyProperties(userDO, loginUser);
                String accessToken = JWTUtil.geneJsonWebToken(loginUser);
                // accesstoken
                // accesstoken expiration time
                // uuid generate refresh token for 30 days
//                String refreshToken = CommonUtil.generateUUID();
//                redisTemplate.opsForValue().set(refreshToken, "1", 1000 * 60 * 60 * 24 * 30);

                return JsonData.buildSuccess(accessToken);
            } else {
                // password wrong
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
            }

        } else {
            // account not exist
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
        }
    }

    /**
     * find user detail
     * @return
     */
    @Override
    public UserVO findUserDetail() {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>().eq("id", loginUser.getId()));

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDO, userVO);

        return userVO;
    }

    /**
     * check if account is unique
     * @param mail
     * @return
     */
    private boolean checkUnique(String mail) {

        QueryWrapper queryWrapper = new QueryWrapper<UserDO>().eq("mail", mail);
        List<UserDO> list = userMapper.selectList(queryWrapper);
        return list.size() == 0;
    }

     /**
     * initilize new user info
     * @param userDO
     */
    private void userRegisterInitTask(UserDO userDO) {

        NewUserCouponRequest request = new NewUserCouponRequest();
        request.setName(userDO.getName());
        request.setUserId(userDO.getId());
        JsonData jsonData = couponFeignService.addNewUserCoupon(request);
        if (jsonData.getCode() != 0) {
            throw new RuntimeException("error when allocate coupon");
        }
        log.info("distribute new user register coupon: {}, result: {}",
                request.toString(), jsonData.toString());
    }
}
