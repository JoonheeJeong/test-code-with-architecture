package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
        Assertions.assertThat(userResponse.id()).isEqualTo(user.getId());
        Assertions.assertThat(userResponse.email()).isEqualTo(user.getEmail());
        Assertions.assertThat(userResponse.nickname()).isEqualTo(user.getNickname());
        Assertions.assertThat(userResponse.status()).isEqualTo(user.getStatus());
        Assertions.assertThat(userResponse.lastLoginAt()).isEqualTo(user.getLastLoginAt());
    }

}