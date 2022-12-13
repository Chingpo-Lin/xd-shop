package org.example.controller;

import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

@Api(tags = "notify module")
@RestController
@RequestMapping("/api/user/v1")
@Slf4j
public class NotifyController {

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * captcha code expire time
     */
    private static final long CAPTCHA_CODE_EXPIRED = 60 * 1000 * 10;

    @ApiOperation("get image captcha")
    @GetMapping("captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        String captchaText = captchaProducer.createText();
        log.info("picture captcha:{}", captchaText);
        
        // store
        redisTemplate.opsForValue().set(getCaptchaKey(request), captchaText,
                CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = captchaProducer.createImage(captchaText);
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error("get captcha err:{}", e);
            e.printStackTrace();
        }
    }

    /**
     * get cache key
     * @param request
     * @return
     */
    private String getCaptchaKey(HttpServletRequest request) {
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        
        String key = "user-service:captcha:" + CommonUtil.MD5(ip + userAgent);
        log.info("ip={}", ip);
        log.info("userAgent={}", userAgent);
        log.info("key={}", key);
        return key;
    }
}
