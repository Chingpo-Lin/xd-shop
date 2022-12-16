package org.example.biz;

import lombok.extern.slf4j.Slf4j;
import org.example.UserApplication;
import org.example.component.MailService;
import org.example.utils.CommonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class MD5Test {

    /**
     * test if cmd5 can decode (yes)
     */
    @Test
    public void testMd5() {
        log.info(CommonUtil.MD5("123456"));
    }
}
