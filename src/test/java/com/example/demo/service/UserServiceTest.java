package com.example.demo.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@SqlGroup({
        @Sql(value = "classpath:sql/test-user-service-init.sql",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:sql/test-user-service-end.sql",
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@TestPropertySource(locations = {"classpath:application.properties"})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepo;

    @DisplayName("getActiveByEmail로 ACTIVE 회원 엔티티를 조회할 수 있다.")
    @Test
    void getActiveByEmail_ok() throws Exception {
        // given
        // when
        UserEntity byEmail = userService.getActiveByEmail("ownsider@naver.com");

        // then
        assertThat(byEmail).isNotNull();
        assertThat(byEmail.getEmail()).isEqualTo("ownsider@naver.com");
        assertThat(byEmail.getNickname()).isEqualTo("ownsider");
        assertThat(byEmail.getAddress()).isEqualTo("Hanam");
        assertThat(byEmail.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(byEmail.getLastLoginAt()).isEqualTo(1);
    }

    @DisplayName("getActiveByEmail은 PENDING 회원 조회는 예외를 발생시킨다")
    @Test
    void getActiveByEmail_failWithPending_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getActiveByEmail("jeonggoo75@gmail.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("getActiveByEmail은 해당 이메일의 회원이 없으면 예외를 발생시킨다")
    @Test
    void getActiveByEmail_failWithNonexistentEmail_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getActiveByEmail("jeonggoo76@gmail.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("getActiveById로 ACTIVE 회원 엔티티를 조회할 수 있다.")
    @Test
    void getActiveById_ok() throws Exception {
        // given
        // when
        UserEntity byEmail = userService.getActiveById(2L);

        // then
        assertThat(byEmail).isNotNull();
        assertThat(byEmail.getEmail()).isEqualTo("ownsider@naver.com");
        assertThat(byEmail.getNickname()).isEqualTo("ownsider");
        assertThat(byEmail.getAddress()).isEqualTo("Hanam");
        assertThat(byEmail.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(byEmail.getLastLoginAt()).isEqualTo(1);
    }

    @DisplayName("getActiveById는 PENDING 회원 조회는 예외를 발생시킨다")
    @Test
    void getActiveById_failWithPending_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getActiveById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("getActiveById는 해당 id의 회원이 없으면 예외를 발생시킨다")
    @Test
    void getActiveById_failWithNonexistentId_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getActiveById(2323L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("create로 회원을 생성할 수 있다")
    @Test
    void create_ok() throws Exception {
        // given
        UserCreateDto dto = UserCreateDto.builder()
                .email("test.email@example.com")
                .address("California")
                .nickname("testnickname")
                .build();

        // when
        UserEntity userEntity = userService.create(dto);

        // then
        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getId()).isEqualTo(3L);
        assertThat(userEntity.getEmail()).isEqualTo("test.email@example.com");
        assertThat(userEntity.getAddress()).isEqualTo("California");
        assertThat(userEntity.getNickname()).isEqualTo("testnickname");
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(userEntity.getCertificationCode()).isNotNull(); // TODO: 직접검증
        assertThat(userEntity.getLastLoginAt()).isNull();
    }

    @DisplayName("update로 ACTIVE 회원을 수정할 수 있다")
    @Test
    void update_ok() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("joonhee")
                .address("Daejeon Doan")
                .build();
        UserEntity updatedUser = userService.update(2L, dto);
        assertThat(updatedUser.getId()).isEqualTo(2L);
        assertThat(updatedUser.getNickname()).isEqualTo("joonhee");
        assertThat(updatedUser.getAddress()).isEqualTo("Daejeon Doan");
    }

    @DisplayName("update로 id가 없는 회원을 수정하려고 하면 ResourceNotFoundException이 발생한다")
    @Test
    void updateNonexistentUser_throwsResourceNotFoundException() throws Exception {
        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("joonhee")
                .address("Daejeon Doan")
                .build();

        assertThatThrownBy(() -> userService.update(2025L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("존재하는 회원은 login 할 수 있다.")
    @Test
    void login_userWithStatusACTIVE_ok() throws Exception {
        // given
        // when
        userService.login(2L);

        // then
        UserEntity userEntity = userRepo.findById(2L).orElseThrow();
        assertThat(userEntity.getLastLoginAt()).isGreaterThan(1L); // TODO: 정확한 시간
    }

    @DisplayName("id로 찾을 수 없는 회원은 login 시도하면 ResourceNotFoundException이 발생한다")
    @Test
    void login_nonExistentUser_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        assertThatThrownBy(() -> userService.login(2025L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("verifyEmail로 회원을 ACTIVE 상태로 변경할 수 있다")
    @Test
    void verifyEmail_makesUserACTIVE() throws Exception {
        // given
        // when
        userService.verifyEmail(1L, "b84b2142-a620-4f95-b317-40f69c64fec8");

        // then
        UserEntity userEntity = userRepo.findById(1L).orElseThrow();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("certificationCode 가 일치하지 않으면 verifyEmail 은 CertificationCodeNotMatchedException 예외를 발생시킨다 ")
    @Test
    void verifyEmail_withWrongCertificationCode_throwsCertificationCodeNotMatchedException() throws Exception {
        // given
        // when
        // then
        assertThatThrownBy(() -> userService.verifyEmail(1L, "b84b2142-a620-4f95-b317-40f69c64fec0"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }

}