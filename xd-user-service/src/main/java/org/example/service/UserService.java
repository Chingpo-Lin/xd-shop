package org.example.service;

import org.example.request.UserLoginRequest;
import org.example.request.UserRegisterRequest;
import org.example.utils.JsonData;

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
}
