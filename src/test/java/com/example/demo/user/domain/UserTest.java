package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @DisplayName("User 를 생성하면 PENDING 상태이고, certificationCode 가 생성된다")
    @Test
    void from_ok() throws Exception {
        // given
        UserCreate dto = UserCreate.builder()
                .email("jeonggoo75@gmail.com")
                .nickname("jeonggoo75")
                .address("Daejeon")
                .build();

        // when
        User user = User.from(dto);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getNickname()).isEqualTo(dto.getNickname());
        assertThat(user.getAddress()).isEqualTo(dto.getAddress());
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getLastLoginAt()).isNull();
        assertThat(user.getCertificationCode()).isNotNull(); // todo: 정확한 값
    }

    @DisplayName("User 의 address 와 nickname 을 변경할 수 있다")
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

        UserUpdate updateDto = UserUpdate.builder()
                .address("Seoul")
                .nickname("joonhee")
                .build();

        // when
        user.update(updateDto);

        // then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("jeonggoo75@gmail.com");
        assertThat(user.getNickname()).isEqualTo(updateDto.getNickname());
        assertThat(user.getAddress()).isEqualTo(updateDto.getAddress());
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getLastLoginAt()).isEqualTo(9L);
    }

    @DisplayName("login 하면 lastLoginAt 이 변경된다")
    @Test
    void login_ok() throws Exception {
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
        user.login();

        // then
        assertThat(user.getLastLoginAt()).isGreaterThan(9L); // TODO: 정확한 값 비교
    }

    @DisplayName("verify 성공하면 ACTIVE 상태로 변경된다")
    @Test
    void verify_ok() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("jeonggoo75@gmail.com")
                .nickname("jeonggoo75")
                .address("Daejeon")
                .certificationCode(UUID.randomUUID().toString())
                .status(UserStatus.PENDING)
                .build();

        // when
        user.verify(user.getCertificationCode());

        // then
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("verify 실패시 CertificationCodeNotMatchedException 발생")
    @Test
    void verify_fail_throwsCertificationCodeNotMatchedException() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("jeonggoo75@gmail.com")
                .nickname("jeonggoo75")
                .address("Daejeon")
                .certificationCode("b84b2142-a620-4f95-b317-40f69c64fec9")
                .status(UserStatus.PENDING)
                .build();

        // when, then
        assertThatThrownBy(() -> user.verify("b84b2142-a620-4f95-b317-40f69c64fec0"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }

}