package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class FastUserControllerTest {

    private UserController userController;

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

        userController = new UserController(testContainer.userService);
    }

    @DisplayName("GET /api/users/{id} 회원 조회시 200 UserResponse")
    @Test
    void getById_ok() {
        // given
        long userId = 2L;

        // when
        ResponseEntity<UserResponse> responseEntity = userController.getById(userId);
        UserResponse userResponse = responseEntity.getBody();

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(userResponse.id()).isEqualTo(userId);
        assertThat(userResponse.email()).isEqualTo("ownsider@naver.com");
        assertThat(userResponse.nickname()).isEqualTo("ownsider");
        assertThat(userResponse.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(userResponse.lastLoginAt()).isEqualTo(1);
    }

    @DisplayName("GET /api/users/{id} 회원 조회시 id 에 해당하는 게시물이 없으면 404 NotFound")
    @Test
    void getById_nonexistentId_404NotFound() {
        // given
        long userId = 3L;

        // when, then
        assertThatThrownBy(() -> userController.getById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("users 에서 id,status 3,ACTIVE 을(를) 찾을 수 없습니다.");
    }

    @DisplayName("GET /api/users/{id}/verify 이메일인증 성공시 Found 응답")
    @Test
    void verifyEmail_ok() {
        // given
        long userId = 1L;
        String certificationCode = "b84b2142-a620-4f95-b317-40f69c64fec8";

        // when
        ResponseEntity<Void> voidResponseEntity = userController.verifyEmail(userId, certificationCode);

        // then
        assertThat(voidResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(302));
        assertThat(voidResponseEntity.getHeaders().get("Location").get(0)).isEqualTo("http://localhost:3000");
    }

    @DisplayName("GET /api/users/{id}/verify 이메일인증 실패시 Forbidden 응답")
    @Test
    void verifyEmail_wrongVerification() throws Exception {
        // given
        long userId = 1L;
        String certificationCode = "b84b2142-a620-4f95-b317-40f69c64fec9";

        // when, then
        assertThatThrownBy(() -> userController.verifyEmail(userId, certificationCode))
                .isInstanceOf(CertificationCodeNotMatchedException.class)
                .hasMessage("자격 증명에 실패하였습니다.");
    }

    @DisplayName("GET /api/users/{id}/verify 요청시 해당 id 회원 없으면 Not Found 응답")
    @Test
    void verifyEmail_nonexistentId_notFound() throws Exception {
        // given
        long userId = 3L;
        String certificationCode = "b84b2142-a620-4f95-b317-40f69c64fec8";

        // when, then
        assertThatThrownBy(() -> userController.verifyEmail(userId, certificationCode))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("users 에서 id 3 을(를) 찾을 수 없습니다.");
    }

    @DisplayName("GET /api/users/me 요청 성공시 Ok, MyProfileResponse 응답")
    @Test
    void getMyInfo_okWithMyProfileResponse() throws Exception {
        // given
        String email = "ownsider@naver.com";

        // when
        ResponseEntity<MyProfileResponse> responseEntity = userController.getMyInfo(email);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        MyProfileResponse body = responseEntity.getBody();
        assertThat(body.id()).isEqualTo(2L);
        assertThat(body.email()).isEqualTo(email);
        assertThat(body.nickname()).isEqualTo("ownsider");
        assertThat(body.address()).isEqualTo("Hanam");
        assertThat(body.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(body.lastLoginAt()).isEqualTo(1756737375719L);
    }

    @DisplayName("GET /api/users/me 요청시 회원 status ACTIVE 아니면 Not Found 응답")
    @Test
    void getMyInfo_nonActiveUser_notFound() throws Exception {
        // given
        String email = "jeonggoo75@gmail.com";

        // when, then
        assertThatThrownBy(() -> userController.getMyInfo(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("users 에서 email,status jeonggoo75@gmail.com,ACTIVE 을(를) 찾을 수 없습니다.");
    }

    @DisplayName("PUT /api/users/me 요청 성공시 Ok, MyProfileResponse 응답")
    @Test
    void updateMyInfo_okWithMyProfileResponse() throws Exception {
        // given
        String email = "ownsider@naver.com";
        UserUpdate dto = UserUpdate.builder()
                .address("LA")
                .nickname("hello")
                .build();

        // when
        ResponseEntity<MyProfileResponse> responseEntity = userController.updateMyInfo(email, dto);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        MyProfileResponse body = responseEntity.getBody();
        assertThat(body.id()).isEqualTo(2L);
        assertThat(body.email()).isEqualTo(email);
        assertThat(body.nickname()).isEqualTo(dto.getNickname());
        assertThat(body.address()).isEqualTo(dto.getAddress());
        assertThat(body.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(body.lastLoginAt()).isEqualTo(1L);
    }

    @DisplayName("PUT /api/users/me 요청시 회원 status ACTIVE 아니면 Not Found 응답")
    @Test
    void updateMyInfo_nonActiveUser_notFound() throws Exception {
        // given
        String email = "jeonggoo75@gmail.com";
        UserUpdate dto = UserUpdate.builder()
                .address("LA")
                .nickname("hello")
                .build();

        // when, then
        assertThatThrownBy(() -> userController.updateMyInfo(email, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("users 에서 email,status jeonggoo75@gmail.com,ACTIVE 을(를) 찾을 수 없습니다.");
    }

}