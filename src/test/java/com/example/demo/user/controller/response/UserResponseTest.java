package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @DisplayName("UserResponse 를 생성할 수 있다")
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

        // when
        UserResponse userResponse = UserResponse.from(user);

        // then
        assertThat(userResponse.id()).isEqualTo(user.getId());
        assertThat(userResponse.email()).isEqualTo(user.getEmail());
        assertThat(userResponse.nickname()).isEqualTo(user.getNickname());
        assertThat(userResponse.status()).isEqualTo(user.getStatus());
        assertThat(userResponse.lastLoginAt()).isEqualTo(user.getLastLoginAt());
    }

}