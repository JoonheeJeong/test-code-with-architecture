package com.example.demo.post.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("slow")
@SpringBootTest
@SqlGroup({
        @Sql(scripts = {"classpath:sql/test-post-service-init.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
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
        Post byId = postService.getById(1L);

        // then
        assertThat(byId).isNotNull();
        assertThat(byId.getId()).isEqualTo(1L);
        assertThat(byId.getContent()).isEqualTo("내용22");
        assertThat(byId.getCreatedAt()).isEqualTo(1755812460000L);
        assertThat(byId.getModifiedAt()).isEqualTo(1755813300000L);
        User writer = userRepository.findById(2L).orElseThrow();
        User writer2 = byId.getWriter();
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

    @DisplayName("create로 게시물을 생성할 수 있다")
    @Test
    void create_ok() throws Exception {
        // given
        PostCreate dto = PostCreate.builder()
                .content("content입니당")
                .writerId(2L)
                .build();

        // when
        Post post = postService.create(dto);

        // then
        assertThat(post).isNotNull();
        assertThat(post.getId()).isEqualTo(2L);
        assertThat(post.getContent()).isEqualTo("content입니당");
        assertThat(post.getCreatedAt()).isNotNull(); // TODO: 직접적인 값 검증 가능하도록 개선 필요
        assertThat(post.getModifiedAt()).isNull();
        assertThat(post.getWriter().getEmail()).isEqualTo("ownsider@naver.com");
    }

    @DisplayName("update로 게시물을 수정할 수 있다")
    @Test
    void update_ok() {
        // given
        PostUpdate dto = PostUpdate.builder()
                .content("modified content")
                .build();

        // when
        Post post = postService.update(1L, dto);

        assertThat(post).isNotNull();
        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getContent()).isEqualTo("modified content");
        assertThat(post.getModifiedAt()).isNotNull(); // TODO: 직접적인 값 검증 가능하도록 개선 필요
    }

}