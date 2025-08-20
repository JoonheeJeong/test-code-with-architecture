package com.example.demo.service;

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

    @DisplayName("getByEmail로 Active 회원 엔티티를 조회할 수 있다.")
    @Test
    void getByEmail_ok() throws Exception {
        // given
        // when
        UserEntity byEmail = userService.getByEmail("ownsider@naver.com");

        // then
        assertThat(byEmail).isNotNull();
        assertThat(byEmail.getEmail()).isEqualTo("ownsider@naver.com");
        assertThat(byEmail.getNickname()).isEqualTo("ownsider");
        assertThat(byEmail.getAddress()).isEqualTo("Hanam");
        assertThat(byEmail.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(byEmail.getLastLoginAt()).isEqualTo(1);
    }

    @DisplayName("getByEmail은 PENDING 회원 조회는 예외를 발생시킨다")
    @Test
    void getByEmail_failWithPending_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getByEmail("jeonggoo75@gmail.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("getByEmail은 해당 이메일의 회원이 없으면 예외를 발생시킨다")
    @Test
    void getByEmail_failWithNonexistentEmail_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getByEmail("jeonggoo76@gmail.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("getById로 Active 회원 엔티티를 조회할 수 있다.")
    @Test
    void getById_ok() throws Exception {
        // given
        // when
        UserEntity byEmail = userService.getById(2L);

        // then
        assertThat(byEmail).isNotNull();
        assertThat(byEmail.getEmail()).isEqualTo("ownsider@naver.com");
        assertThat(byEmail.getNickname()).isEqualTo("ownsider");
        assertThat(byEmail.getAddress()).isEqualTo("Hanam");
        assertThat(byEmail.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(byEmail.getLastLoginAt()).isEqualTo(1);
    }

    @DisplayName("getById는 PENDING 회원 조회는 예외를 발생시킨다")
    @Test
    void getById_failWithPending_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("getById는 해당 id의 회원이 없으면 예외를 발생시킨다")
    @Test
    void getById_failWithNonexistentId_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getById(2323L))
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

    @DisplayName("update로 PENDING 회원을 수정하면 ResourceNotFoundException이 발생한다")
    @Test
    void updatePendingUser_throwsResourceNotFoundException() throws Exception {
        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("joonhee")
                .address("Daejeon Doan")
                .build();

        assertThatThrownBy(() -> userService.update(1L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("update로 ID가 없는 회원을 수정하려고 하면 ResourceNotFoundException이 발생한다")
    @Test
    void updateNonexistentUser_throwsResourceNotFoundException() throws Exception {
        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("joonhee")
                .address("Daejeon Doan")
                .build();

        assertThatThrownBy(() -> userService.update(2025L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("ACTIVE 회원은 login 할 수 있다.")
    @Test
    void login_userWithStatusACTIVE_ok() throws Exception {
        // given
        // when
        userService.login(2L);

        // then
        UserEntity userEntity = userRepo.findById(2L).orElseThrow();
        assertThat(userEntity.getLastLoginAt()).isGreaterThan(1L);
    }

    @DisplayName("PENDING 회원은 login 시도하면 ResourceNotFoundException이 발생한다")
    @Test
    void login_userWithStatusPEDNING_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        assertThatThrownBy(() -> userService.login(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("ID로 찾을 수 없는 회원은 login 시도하면 ResourceNotFoundException이 발생한다")
    @Test
    void login_nonExistentUser_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        assertThatThrownBy(() -> userService.login(2025L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}