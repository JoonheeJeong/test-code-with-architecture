package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import lombok.AccessLevel;
import lombok.Builder;

public record UserResponse(
        Long id,
        String email,
        String nickname,
        UserStatus status,
        Long lastLoginAt
) {

    @Builder(access = AccessLevel.PRIVATE)
    public UserResponse {
    }

    public static UserResponse from(User user) {
        return builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

}
