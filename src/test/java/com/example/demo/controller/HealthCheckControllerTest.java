package com.example.demo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest(classes = HealthCheckController.class) // 컨텍스트 재활용을 위해 별도로 지정하지 않는 게 좋다!
@SpringBootTest
@AutoConfigureMockMvc
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("헬스 체크 ok")
    @Test
    void healthCheck_ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/health_check.html"))
                .andExpect(status().isOk());
    }

}