package com.example.demo.post.controller;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class FastCreateControllerTest {

    private PostCreateController postCreateController;

    @BeforeEach
    void setUp() {
        TestContainer testContainer = TestContainer.builder()
                .clockProvider(() -> 1234L)
                .build();

        UserRepository userRepo = testContainer.userRepo;
        PostRepository postRepo = testContainer.postRepo;

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

        postRepo.save(Post.builder()
                .id(1L)
                .content("내용22")
                .createdAt(1755812460000L)
                .modifiedAt(1755813300000L)
                .writer(user2)
                .build());

        postCreateController = new PostCreateController(testContainer.postService);
    }

    @DisplayName("POST /api/posts 요청으로 게시물을 생성할 수 있다")
    @Test
    void create_ok() throws Exception {
        // given
        PostCreate dto = PostCreate.builder()
                .content("내용 테스트")
                .writerId(2L)
                .build();

        // when
        ResponseEntity<PostResponse> responseEntity = postCreateController.create(dto);

        // then
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        PostResponse body = responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.id()).isEqualTo(2);
        assertThat(body.content()).isEqualTo("내용 테스트");
        assertThat(body.createdAt()).isEqualTo(1234L);
        assertThat(body.modifiedAt()).isNull();

        UserResponse writer = body.writer();
        assertThat(writer).isNotNull();
        assertThat(writer.id()).isEqualTo(2);
        assertThat(writer.email()).isEqualTo("ownsider@naver.com");
        assertThat(writer.nickname()).isEqualTo("ownsider");
        assertThat(writer.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(writer.lastLoginAt()).isEqualTo(1L);
    }

    @DisplayName("POST /api/posts 요청으로 게시물을 생성시 회원이 없으면 404 Not Found")
    @Test
    void create_unknownUser_404notFound() throws Exception {
        // given
        PostCreate dto = PostCreate.builder()
                .content("내용 테스트")
                .writerId(3L)
                .build();

        // when, then
        assertThatThrownBy(() -> postCreateController.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("users 에서 id 3 을(를) 찾을 수 없습니다.");
    }
}