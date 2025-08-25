package com.example.demo.user.service.port;

public interface MailSender {

    void send(String receiverEmail, String subject, String content);
}
