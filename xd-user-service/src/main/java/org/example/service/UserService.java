package org.example.service;

import org.example.request.UserLoginRequest;
import org.example.request.UserRegisterRequest;
import org.example.utils.JsonData;
import org.example.vo.UserVO;

public interface UserService {

    /**
     * user register
     * @param registerRequest
     * @return
     */
    JsonData register(UserRegisterRequest registerRequest);

    /**
     * user login
     * @param userLoginRequest
     * @return
     */
    JsonData login(UserLoginRequest userLoginRequest);

    /**
     * find user detail
     * @return
     */
    UserVO findUserDetail();
}
