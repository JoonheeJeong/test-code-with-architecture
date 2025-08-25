package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import lombok.AccessLevel;
import lombok.Builder;

public record MyProfileResponse(
        Long id,
        String email,
        String nickname,
        String address,
        UserStatus status,
        Long lastLoginAt
) {

    @Builder(access = AccessLevel.PRIVATE)
    public MyProfileResponse {
    }

    public static MyProfileResponse from(User user) {
        return MyProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .address(user.getAddress())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
