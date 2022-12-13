package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    /**
     * springboot will auto read config in yml file
     */
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    /**
     * send mail
     * @param to
     * @param subject
     * @param content
     */
    @Override
    public void sendMail(String to, String subject, String content) {
        // create mail msg obj
        SimpleMailMessage msg = new SimpleMailMessage();

        // mail sender
        msg.setFrom(from);

        // mail receiver
        msg.setTo(to);

        // mail title
        msg.setSubject(subject);

        // mail text
        msg.setText(content);

        mailSender.send(msg);
        log.info("send mail successfully:{}", msg);
    }
}
