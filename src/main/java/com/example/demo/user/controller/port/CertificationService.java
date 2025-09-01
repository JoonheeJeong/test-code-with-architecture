package com.example.demo.user.controller.port;

public interface CertificationService {
    void send(Long id, String certificationCode, String email);
}
