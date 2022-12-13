package org.example.service;

public interface MailService {

    /**]
     * send mail
     * @param to
     * @param subject
     * @param content
     */
    void sendMail(String to, String subject, String content);
}
