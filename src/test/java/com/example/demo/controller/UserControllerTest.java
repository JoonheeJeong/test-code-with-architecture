package com.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(scripts = {"classpath:sql/test-user-controller-init.sql"}, executionPhase =
                Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {"classpath:sql/test-user-controller-end.sql"},
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("GET /api/users/{id} 회원 조회시 200 UserResponse")
    @Test
    void getById_ok() throws Exception {
        // given
        long userId = 2L;

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("ownsider@naver.com"))
                .andExpect(jsonPath("$.nickname").value("ownsider"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.lastLoginAt").value(1))
        ;
    }

    @DisplayName("GET /api/users/{id} 회원 조회시 id 에 해당하는 게시물이 없으면 404 NotFound")
    @Test
    void getById_nonexistentId_404NotFound() throws Exception {
        // given
        long userId = 3L;

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID %d를 찾을 수 없습니다.".formatted(userId)))
        ;
    }

    @DisplayName("GET /api/users/{id}/verify 이메일인증 성공시 Found 응답")
    @Test
    void verifyEmail_ok() throws Exception {
        // given
        long userId = 1L;
        String certificationCode = "b84b2142-a620-4f95-b317-40f69c64fec8";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}/verify", userId)
                        .queryParam("certificationCode", certificationCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost:3000"))
        ;
    }

    @DisplayName("GET /api/users/{id}/verify 이메일인증 실패시 Forbidden 응답")
    @Test
    void verifyEmail_wrongVerification() throws Exception {
        // given
        long userId = 1L;
        String certificationCode = "b84b2142-a620-4f95-b317-40f69c64fec9";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}/verify", userId)
                        .queryParam("certificationCode", certificationCode))
                .andExpect(status().isForbidden())
                .andExpect(content().string("자격 증명에 실패하였습니다."))
        ;
    }

    @DisplayName("GET /api/users/{id}/verify 요청시 해당 id 회원 없으면 Not Found 응답")
    @Test
    void verifyEmail_nonexistentId_notFound() throws Exception {
        // given
        long userId = 3L;
        String certificationCode = "b84b2142-a620-4f95-b317-40f69c64fec8";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}/verify", userId)
                        .queryParam("certificationCode", certificationCode))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID %d를 찾을 수 없습니다.".formatted(userId)))
        ;
    }

}