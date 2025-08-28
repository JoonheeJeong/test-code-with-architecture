package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.infrastructure.SystemClockProvider;
import com.example.demo.common.infrastructure.SystemUUIDProvider;
import com.example.demo.common.service.port.ClockProvider;
import com.example.demo.common.service.port.UUIDProvider;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.UserFakeRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.port.MailSender;
import com.example.demo.user.service.port.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class FastUserServiceTest {

    private UserService userService;
    private UserFakeRepository userRepo;

    @BeforeEach
    void init() {
        userRepo = new UserFakeRepository();
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

        MailSender mailSender = new FakeMailSender();
        ClockProvider clockProvider = new SystemClockProvider();
        UUIDProvider uuidProvider = new SystemUUIDProvider();
        CertificationServiceImpl certificationService = new CertificationServiceImpl(mailSender);
        userService = new UserServiceImpl(userRepo, certificationService, clockProvider, uuidProvider);
    }

    @DisplayName("getActiveByEmail로 ACTIVE 회원 엔티티를 조회할 수 있다.")
    @Test
    void getActiveByEmail_ok() throws Exception {
        // given
        // when
        User user = userService.getActiveByEmail("ownsider@naver.com");

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("ownsider@naver.com");
        assertThat(user.getNickname()).isEqualTo("ownsider");
        assertThat(user.getAddress()).isEqualTo("Hanam");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getLastLoginAt()).isEqualTo(1);
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
        User user = userService.getActiveById(2L);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("ownsider@naver.com");
        assertThat(user.getNickname()).isEqualTo("ownsider");
        assertThat(user.getAddress()).isEqualTo("Hanam");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getLastLoginAt()).isEqualTo(1);
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
        UserCreate dto = UserCreate.builder()
                .email("test.email@example.com")
                .address("California")
                .nickname("testnickname")
                .build();

        // when
        User user = userService.create(dto);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(3L);
        assertThat(user.getEmail()).isEqualTo("test.email@example.com");
        assertThat(user.getAddress()).isEqualTo("California");
        assertThat(user.getNickname()).isEqualTo("testnickname");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isNotNull(); // TODO: 직접검증
        assertThat(user.getLastLoginAt()).isNull();
    }

    @DisplayName("update로 ACTIVE 회원을 수정할 수 있다")
    @Test
    void update_ok() {
        UserUpdate dto = UserUpdate.builder()
                .nickname("joonhee")
                .address("Daejeon Doan")
                .build();
        User user = userService.update(2L, dto);
        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getNickname()).isEqualTo("joonhee");
        assertThat(user.getAddress()).isEqualTo("Daejeon Doan");
    }

    @DisplayName("update로 id가 없는 회원을 수정하려고 하면 ResourceNotFoundException이 발생한다")
    @Test
    void updateNonexistentUser_throwsResourceNotFoundException() throws Exception {
        UserUpdate dto = UserUpdate.builder()
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
        User user = userRepo.findById(2L).orElseThrow();
        assertThat(user.getLastLoginAt()).isGreaterThan(1L); // TODO: 정확한 시간
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
        User user = userRepo.findById(1L).orElseThrow();
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
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