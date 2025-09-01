package com.example.demo.user.controller;

import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class FastUserCreateControllerTest {

    private UserCreateController userCreateController;

    @BeforeEach
    void setUp() {
        TestContainer testContainer = TestContainer.builder()
                .clockProvider(() -> 1756737375719L)
                .build();

        UserRepository userRepo = testContainer.userRepo;

        User user1 = User.builder()
                .id(1L)
                .email("jeonggoo75@gmail.com")
                .nickname("jeonggoo75")
                .address("Daejeon")
                .certificationCode("b84b2142-a620-4f95-b317-40f69c64fec8")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build();
        userRepo.save(user1);

        User user2 = User.builder()
                .id(2L)
                .email("ownsider@naver.com")
                .nickname("ownsider")
                .address("Hanam")
                .certificationCode("b84b2142-a620-4f95-b317-40f69c64fec9")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(1L)
                .build();
        userRepo.save(user2);

        userCreateController = new UserCreateController(testContainer.userService);
    }

    @DisplayName("POST /api/users 로 회원을 생성 성공시 201 및 UserResponse 응답")
    @Test
    void create_createdWithUserResponse() throws Exception {
        // given
        UserCreate dto = UserCreate.builder()
                .email("test.email@example.com")
                .nickname("test")
                .address("Seoul")
                .build();

        // when
        ResponseEntity<UserResponse> responseEntity = userCreateController.create(dto);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));

        UserResponse body = responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.id()).isEqualTo(3);
        assertThat(body.email()).isEqualTo(dto.getEmail());
        assertThat(body.nickname()).isEqualTo(dto.getNickname());
        assertThat(body.status()).isEqualTo(UserStatus.PENDING);
        assertThat(body.lastLoginAt()).isNull();
    }

}