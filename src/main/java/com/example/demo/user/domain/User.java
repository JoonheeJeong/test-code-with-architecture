package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import lombok.Builder;
import lombok.Getter;

import java.time.Clock;
import java.util.UUID;

@Getter
public class User {
    private final Long id;
    private final String email;
    private String nickname;
    private String address;
    private String certificationCode;
    private UserStatus status;
    private Long lastLoginAt;

    @Builder
    public User(Long id, String email, String nickname, String address, String certificationCode, UserStatus status,
                Long lastLoginAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.address = address;
        this.certificationCode = certificationCode;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
    }

    public static User from(UserCreate userCreate) {
        return builder()
                .email(userCreate.getEmail())
                .nickname(userCreate.getNickname())
                .address(userCreate.getAddress())
                .status(UserStatus.PENDING)
                .certificationCode(UUID.randomUUID().toString())
                .build();
    }

    public void update(UserUpdate userUpdate) {
        this.nickname = userUpdate.getNickname();
        this.address = userUpdate.getAddress();
    }

    public void login() {
        this.lastLoginAt = Clock.systemUTC().millis();
    }

    public void verify(String certificationCode) {
        if (!this.certificationCode.equals(certificationCode)) {
            throw new CertificationCodeNotMatchedException();
        }
        this.status = UserStatus.ACTIVE;
    }

}
