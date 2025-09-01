package com.example.demo.post.controller;

import com.example.demo.post.domain.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("slow")
@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(scripts = {"classpath:sql/test-post-controller-init.sql"}, executionPhase =
                Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {"classpath:sql/test-post-controller-end.sql"},
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("POST /api/posts 요청으로 게시물을 생성할 수 있다")
    @Test
    void create_ok() throws Exception {
        PostCreate dto = PostCreate.builder()
                .content("내용 테스트")
                .writerId(2L)
                .build();
        String requestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.content").value("내용 테스트"))
                .andExpect(jsonPath("$.createdAt").exists()) // TODO: 정확히 일치하려면?
                .andExpect(jsonPath("$.modifiedAt").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.writer.id").value(2))
                .andExpect(jsonPath("$.writer.email").value("ownsider@naver.com"))
                .andExpect(jsonPath("$.writer.nickname").value("ownsider"))
                .andExpect(jsonPath("$.writer.status").value("ACTIVE"))
                .andExpect(jsonPath("$.writer.lastLoginAt").exists()) // TODO: 정확히 일치하려면?
        ;
    }

    @DisplayName("POST /api/posts 요청으로 게시물을 생성시 회원이 없으면 404 Not Found")
    @Test
    void create_unknownUser_404notFound() throws Exception {
        PostCreate dto = PostCreate.builder()
                .content("내용 테스트")
                .writerId(3L)
                .build();
        String requestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("users 에서 id 3 을(를) 찾을 수 없습니다."));
    }
}