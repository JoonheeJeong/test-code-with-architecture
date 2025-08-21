package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PostEntity;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@SqlGroup({
        @Sql(scripts = {"classpath:sql/test-post-service-init.sql"}, executionPhase =
                Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {"classpath:sql/test-post-service-end.sql"},
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("getById로 게시물을 조회할 수 있다")
    @Test
    void getById_ok() throws Exception {
        // given
        // when
        PostEntity byId = postService.getById(1L);

        // then
        assertThat(byId).isNotNull();
        assertThat(byId.getId()).isEqualTo(1L);
        assertThat(byId.getContent()).isEqualTo("내용22");
        assertThat(byId.getCreatedAt()).isEqualTo(1755812460000L);
        assertThat(byId.getModifiedAt()).isEqualTo(1755813300000L);
        UserEntity writer = userRepository.findById(2L).orElseThrow();
        UserEntity writer2 = byId.getWriter();
//        assertThat(writer2).isEqualTo(writer); // 트랜잭션 없으면 오류 발생. 사실 굳이 테스트할 필요 없는 부분임
        assertThat(writer.getId()).isEqualTo(writer2.getId());
        assertThat(writer.getEmail()).isEqualTo(writer2.getEmail());
    }

    @DisplayName("getById 로 존재하지 않는 게시물을 조회하면 ResourceNotFoundException 발생")
    @Test
    void getById_nonExistentOne_throwsResourceNotFoundException() throws Exception {
        // given
        // when
        // then
        assertThatThrownBy(() -> postService.getById(2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}