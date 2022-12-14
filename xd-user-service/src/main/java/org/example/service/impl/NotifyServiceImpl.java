package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.CacheKey;
import org.example.enums.BizCodeEnum;
import org.example.enums.SendCodeEnum;
import org.example.component.MailService;
import org.example.service.NotifyService;
import org.example.utils.CheckUtil;
import org.example.utils.CommonUtil;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private MailService mailService;

    private static final String SUBJECT = "xd-shop confirmation code";

    private static final String CONTENT = "Your confirmation code is: %s, expire in 60s";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * expire after 5 mins
     */
    private static final int CODE_EXPIRED = 60 * 1000 * 5;

    /**
     * 1. if not send to a same user:
     * 2. store in cache
     * 3. send code to mail
     * 4. store send record
     * @param sendCodeEnum
     * @param to
     * @return
     */
    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {

        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY, SendCodeEnum.USER_REGISTER, to);

        String cacheValue = redisTemplate.opsForValue().get(cacheKey);

        // if not empty, see if send again in 300s
        if (!StringUtils.isEmpty(cacheValue)) {
            long ttl = Long.parseLong(cacheValue.split("_")[1]);
            // current timestamp - timestamp that time if < 60s then send too frequent
            if (CommonUtil.getCurrentTimestamp() - ttl < 1000 * 60) {
                log.info("send code too frequent");
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }
        // combine code with system time
        String code = CommonUtil.getRandomCode(6);
        String value = code + "_" + CommonUtil.getCurrentTimestamp();
        redisTemplate.opsForValue().set(cacheKey, value, CODE_EXPIRED, TimeUnit.MILLISECONDS);

        if (CheckUtil.isEmail(to)) {
            // if mail
            mailService.sendMail(to, SUBJECT, String.format(CONTENT, code));
            return JsonData.buildSuccess();
        } else if (CheckUtil.isPhone(to)) {
            // if phone
        }

        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }
}
