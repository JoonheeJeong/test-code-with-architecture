package com.example.demo.user.service;

import com.example.demo.user.controller.port.CertificationService;
import com.example.demo.user.service.port.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final MailSender mailSender;

    @Override
    public void send(Long id, String certificationCode, String email) {
        String certificationUrl = generateCertificationUrl(id, certificationCode);
        mailSender.send(email, "Please certify your email address",
                "Please click the following link to certify your email address: " + certificationUrl);
    }

    private String generateCertificationUrl(Long userId, String code) {
        return "http://localhost:8080/api/users/%s/verify?certificationCode=%s".formatted(userId, code);
    }

}
