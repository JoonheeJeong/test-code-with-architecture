package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("slow")
@DataJpaTest
@SqlGroup({
        @Sql(value = "classpath:sql/test-user-init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository repo;

    @AfterEach
    public void tearDown() {
        repo.deleteAll();
    }

    @DisplayName("findByIdAndStatus로 유저 데이터 조회가 가능하다")
    @Test
    void ok_findByIdAndStatus() throws Exception {
        // given
        // when
        Optional<UserEntity> byIdAndStatus = repo.findByIdAndStatus(1L, UserStatus.PENDING);

        // then
        assertThat(byIdAndStatus.isPresent()).isTrue();
    }

    @DisplayName("findByIdAndStatus로 유저 데이터 조회 실패시 Optional.empty()가 반환된다")
    @Test
    void fail_findByIdAndStatus() throws Exception {
        // given
        // when
        Optional<UserEntity> byIdAndStatus = repo.findByIdAndStatus(1L, UserStatus.ACTIVE);

        // then
        assertThat(byIdAndStatus.isEmpty()).isTrue();
    }

    @DisplayName("findByEmailAndStatus로 유저 데이터 조회가 가능하다")
    @Test
    void ok_findByEmailAndStatus() throws Exception {
        // given
        // when
        Optional<UserEntity> byEmailAndStatus = repo.findByEmailAndStatus("jeonggoo75@gmail.com", UserStatus.PENDING);

        // then
        assertThat(byEmailAndStatus.isPresent()).isTrue();
    }

    @DisplayName("findByEmailAndStatus로 유저 데이터 조회 실패시 Optional.empty()가 반환된다")
    @Test
    void fail_findByEmailAndStatus() throws Exception {
        // given
        // when
        Optional<UserEntity> byEmailAndStatus = repo.findByEmailAndStatus("jeonggoo75@gmail.com", UserStatus.ACTIVE);

        // then
        assertThat(byEmailAndStatus.isEmpty()).isTrue();
    }
}