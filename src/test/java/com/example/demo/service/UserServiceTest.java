package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.repository.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;

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

}