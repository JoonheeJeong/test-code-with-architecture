package com.example.demo.user.infrastructure;

import com.example.demo.user.service.port.MailSender;

public class FakeMailSender implements MailSender {

    public String receiverEmail;
    public String subject;
    public String content;

    @Override
    public void send(String receiverEmail, String subject, String content) {
        this.receiverEmail = receiverEmail;
        this.subject = subject;
        this.content = content;
    }

}
