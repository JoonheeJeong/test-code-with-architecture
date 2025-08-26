package com.example.demo.post.controller;

import com.example.demo.post.domain.PostUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private ObjectMapper objectMapper;

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

    @DisplayName("PUT /api/posts/{id} 요청으로 게시물을 수정할 수 있다")
    @Test
    void update_ok() throws Exception {
        // given
        long postId = 1L;
        PostUpdate dto = PostUpdate.builder()
                .content("내용 수정 테스트")
                .build();
        String requestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/%d".formatted(postId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value(dto.getContent()))
                .andExpect(jsonPath("$.createdAt").value(1755812460000L))
                .andExpect(jsonPath("$.modifiedAt").exists()) // TODO: 정확히 일치하려면?
                .andExpect(jsonPath("$.writer.id").value(2))
                .andExpect(jsonPath("$.writer.email").value("ownsider@naver.com"))
                .andExpect(jsonPath("$.writer.nickname").value("ownsider"))
                .andExpect(jsonPath("$.writer.status").value("ACTIVE"))
                .andExpect(jsonPath("$.writer.lastLoginAt").value(1))
        ;
    }

    @DisplayName("PUT /api/posts/{id} 요청은 id 해당 게시물이 없으면 404 Not Found")
    @Test
    void update_nonexistentId_404NotFound() throws Exception {
        // given
        long postId = 3L;
        PostUpdate dto = PostUpdate.builder()
                .content("내용 수정 테스트")
                .build();
        String requestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/%d".formatted(postId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Posts에서 ID %d를 찾을 수 없습니다.".formatted(postId)))
        ;
    }

}