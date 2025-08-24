package com.example.demo.controller;

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
        @Sql(scripts = {"classpath:sql/test-post-controller-init.sql"}, executionPhase =
                Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {"classpath:sql/test-post-controller-end.sql"},
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender javaMailSender;

    @DisplayName("GET /api/posts/{id} 로 게시물을 조회할 수 있다")
    @Test
    void getById_ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("내용22"))
                .andExpect(jsonPath("$.createdAt").value(1755812460000L))
                .andExpect(jsonPath("$.modifiedAt").value(1755813300000L))
                .andExpect(jsonPath("$.writer.id").value(2))
                .andExpect(jsonPath("$.writer.email").value("ownsider@naver.com"))
                .andExpect(jsonPath("$.writer.nickname").value("ownsider"))
                .andExpect(jsonPath("$.writer.status").value("ACTIVE"))
                .andExpect(jsonPath("$.writer.lastLoginAt").exists()) // TODO: 정확히 일치하려면?
        ;
    }

    @DisplayName("GET /api/posts/{id} 로 게시물 조회시 id 에 해당하는 게시물이 없으면 404 NotFound")
    @Test
    void getById_nonexistentId_404NotFound() throws Exception {
        // given
        long id = 3L;

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Posts에서 ID %d를 찾을 수 없습니다.".formatted(id)))
        ;
    }

}