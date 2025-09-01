package com.example.demo.user.service.port;

public interface CertificationService {
    void send(Long id, String certificationCode, String email);
}
