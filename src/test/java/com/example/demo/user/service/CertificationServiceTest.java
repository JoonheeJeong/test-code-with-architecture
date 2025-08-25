package com.example.demo.user.service;

import com.example.demo.user.infrastructure.FakeMailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CertificationServiceTest {

    @DisplayName("인증 메일을 보낼 수 있다")
    @Test
    void send_ok() throws Exception {
        // given
        FakeMailSender mailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationServiceImpl(mailSender);
        Long userId = 1L;
        String certificationCode = "b84b2142-a620-4f95-b317-40f69c64fec8";
        String receiverEmail = "jeonggoo75@gmail.com";

        // when
        certificationService.send(1L, certificationCode, receiverEmail);

        // then
        assertThat(mailSender.receiverEmail).isEqualTo(receiverEmail);
        assertThat(mailSender.subject).isEqualTo("Please certify your email address");
        assertThat(mailSender.content).isEqualTo("Please click the following link to certify your email " +
                "address: http://localhost:8080/api/users/%s/verify?certificationCode=%s"
                        .formatted(userId, certificationCode));
    }
}