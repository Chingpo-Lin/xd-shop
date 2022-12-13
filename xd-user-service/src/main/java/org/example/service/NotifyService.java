package org.example.service;

import org.example.enums.SendCodeEnum;
import org.example.utils.JsonData;

public interface NotifyService {

    JsonData sendCode(SendCodeEnum sendCodeEnum, String to);

}
