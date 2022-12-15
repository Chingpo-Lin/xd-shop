package org.example.service;

import org.example.enums.SendCodeEnum;
import org.example.utils.JsonData;

public interface NotifyService {

    /**
     * send code
     * @param sendCodeEnum
     * @param to
     * @return
     */
    JsonData sendCode(SendCodeEnum sendCodeEnum, String to);

    /**
     * check code
     * @return
     */
    boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code);

}
