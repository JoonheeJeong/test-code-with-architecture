package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @DisplayName("Post 를 생성할 수 있다")
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

        PostCreate createDto = PostCreate.builder()
                .content("content")
                .writerId(1L)
                .build();

        // when
        Post post = Post.from(createDto, user);

        // then
        assertThat(post).isNotNull();
        assertThat(post.getId()).isNull();
        assertThat(post.getContent()).isEqualTo("content");
        assertThat(post.getWriter().getId()).isEqualTo(user.getId());
    }

    @DisplayName("Post 의 content 를 변경할 수 있다")
    @Test
    void update_ok() throws Exception {
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

        PostUpdate updateDto = PostUpdate.builder()
                .content("수정된 내용")
                .build();

        // when
        post.update(updateDto);

        // then
        assertThat(post).isNotNull();
        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getContent()).isEqualTo(updateDto.getContent());
        assertThat(post.getWriter().getId()).isEqualTo(user.getId());
    }

}