package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.infrastructure.SystemClockProvider;
import com.example.demo.common.infrastructure.SystemUUIDProvider;
import com.example.demo.common.service.port.ClockProvider;
import com.example.demo.common.service.port.UUIDProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private ClockProvider clockProvider = new SystemClockProvider();
    private UUIDProvider uuidProvider = new SystemUUIDProvider();

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
        String certificationCode = uuidProvider.random();
        User user = User.from(dto, certificationCode);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getNickname()).isEqualTo(dto.getNickname());
        assertThat(user.getAddress()).isEqualTo(dto.getAddress());
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getLastLoginAt()).isNull();
        assertThat(user.getCertificationCode()).isEqualTo(certificationCode);
    }

    @DisplayName("User 의 address 와 nickname 을 변경할 수 있다")
    @Test
    void update_ok() throws Exception {
        // given
        String certificationCode = uuidProvider.random();

        User user = User.builder()
                .id(1L)
                .email("jeonggoo75@gmail.com")
                .nickname("jeonggoo75")
                .address("Daejeon")
                .certificationCode(certificationCode)
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
        assertThat(user.getCertificationCode()).isEqualTo(certificationCode);
        assertThat(user.getLastLoginAt()).isEqualTo(9L);
    }

    @DisplayName("login 하면 lastLoginAt 이 변경된다")
    @Test
    void login_ok() throws Exception {
        // given
        String certificationCode = uuidProvider.random();
        User user = User.builder()
                .id(1L)
                .email("jeonggoo75@gmail.com")
                .nickname("jeonggoo75")
                .address("Daejeon")
                .certificationCode(certificationCode)
                .status(UserStatus.ACTIVE)
                .lastLoginAt(9L)
                .build();

        // when
        long now = clockProvider.nowMillis();
        user.login(now);

        // then
        assertThat(user.getLastLoginAt()).isEqualTo(now);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("jeonggoo75@gmail.com");
        assertThat(user.getNickname()).isEqualTo("jeonggoo75");
        assertThat(user.getAddress()).isEqualTo("Daejeon");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCertificationCode()).isEqualTo(certificationCode);
    }

    @DisplayName("verify 성공하면 ACTIVE 상태로 변경된다")
    @Test
    void verify_ok() throws Exception {
        // given
        String certificationCode = uuidProvider.random();
        User user = User.builder()
                .id(1L)
                .email("jeonggoo75@gmail.com")
                .nickname("jeonggoo75")
                .address("Daejeon")
                .certificationCode(certificationCode)
                .status(UserStatus.PENDING)
                .build();

        // when
        user.verify(user.getCertificationCode());

        // then
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("jeonggoo75@gmail.com");
        assertThat(user.getNickname()).isEqualTo("jeonggoo75");
        assertThat(user.getAddress()).isEqualTo("Daejeon");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCertificationCode()).isEqualTo(certificationCode);
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