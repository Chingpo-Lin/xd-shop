package org.example.service.impl;

import org.example.enums.BizCodeEnum;
import org.example.enums.SendCodeEnum;
import org.example.component.MailService;
import org.example.service.NotifyService;
import org.example.utils.CheckUtil;
import org.example.utils.CommonUtil;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private MailService mailService;

    private static final String SUBJECT = "xd-shop confirmation code";

    private static final String CONTENT = "Your confirmation code is: %s, expire in 60s";

    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {

        if (CheckUtil.isEmail(to)) {
            // if mail
            String code = CommonUtil.getRandomCode(6);
            mailService.sendMail(to, SUBJECT, String.format(CONTENT, code));
            return JsonData.buildSuccess();
        } else if (CheckUtil.isPhone(to)) {
            // if phone
        }

        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }
}
