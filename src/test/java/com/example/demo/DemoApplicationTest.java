package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class DemoApplicationTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @DisplayName("context load")
    @Test
    void contextLoads() throws Exception {
    }

}