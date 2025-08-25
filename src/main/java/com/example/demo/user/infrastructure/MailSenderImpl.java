package com.example.demo.user.infrastructure;

import com.example.demo.user.service.port.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MailSenderImpl implements MailSender {

    private final ExecutorService executorService;
    private final JavaMailSender mailSender;

    public MailSenderImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void send(String receiverEmail, String subject, String content) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
        executorService.submit(() -> mailSender.send(mailMessage));
    }
}
