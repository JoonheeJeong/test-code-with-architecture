package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
        Assertions.assertThat(postResponse.id()).isEqualTo(post.getId());
        Assertions.assertThat(postResponse.content()).isEqualTo(post.getContent());
        Assertions.assertThat(postResponse.createdAt()).isEqualTo(post.getCreatedAt());
        Assertions.assertThat(postResponse.modifiedAt()).isEqualTo(post.getModifiedAt());
        Assertions.assertThat(postResponse.writer().id()).isEqualTo(post.getWriter().getId());
        Assertions.assertThat(postResponse.writer().email()).isEqualTo(post.getWriter().getEmail());
        Assertions.assertThat(postResponse.writer().nickname()).isEqualTo(post.getWriter().getNickname());
        Assertions.assertThat(postResponse.writer().status()).isEqualTo(post.getWriter().getStatus());
        Assertions.assertThat(postResponse.writer().lastLoginAt()).isEqualTo(post.getWriter().getLastLoginAt());
    }
}