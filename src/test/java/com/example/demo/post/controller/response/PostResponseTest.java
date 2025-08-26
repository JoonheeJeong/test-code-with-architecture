package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PostResponseTest {

    @DisplayName("PostResponse 를 생성할 수 있다")
    @Test
    void from_ok() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("jeonggoo75@gmail.com")
                .nickname("jeonggoo75")
                .address("Daejeon")
                .certificationCode(UUID.randomUUID().toString())
                .status(UserStatus.ACTIVE)
                .lastLoginAt(9L)
                .build();

        Post post = Post.builder()
                .id(1L)
                .content("내용")
                .createdAt(10L)
                .modifiedAt(11L)
                .writer(user)
                .build();

        // when
        PostResponse postResponse = PostResponse.from(post);

        // then
        assertThat(postResponse.id()).isEqualTo(post.getId());
        assertThat(postResponse.content()).isEqualTo(post.getContent());
        assertThat(postResponse.createdAt()).isEqualTo(post.getCreatedAt());
        assertThat(postResponse.modifiedAt()).isEqualTo(post.getModifiedAt());
        assertThat(postResponse.writer().id()).isEqualTo(post.getWriter().getId());
        assertThat(postResponse.writer().email()).isEqualTo(post.getWriter().getEmail());
        assertThat(postResponse.writer().nickname()).isEqualTo(post.getWriter().getNickname());
        assertThat(postResponse.writer().status()).isEqualTo(post.getWriter().getStatus());
        assertThat(postResponse.writer().lastLoginAt()).isEqualTo(post.getWriter().getLastLoginAt());
    }
}