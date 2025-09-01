package com.example.demo.post.controller;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("fast")
class FastPostControllerTest {

    private PostController postController;

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

        postController = new PostController(testContainer.postService);
    }

    @DisplayName("GET /api/posts/{id} 로 게시물을 조회할 수 있다")
    @Test
    void getById_ok() throws Exception {
        // given
        long id = 1L;

        // when
        ResponseEntity<PostResponse> responseEntity = postController.getById(id);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        PostResponse body = responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.id()).isEqualTo(1);
        assertThat(body.content()).isEqualTo("내용22");
        assertThat(body.createdAt()).isEqualTo(1755812460000L);
        assertThat(body.modifiedAt()).isEqualTo(1755813300000L);

        UserResponse writer = body.writer();
        assertThat(writer).isNotNull();
        assertThat(writer.id()).isEqualTo(2);
        assertThat(writer.email()).isEqualTo("ownsider@naver.com");
        assertThat(writer.nickname()).isEqualTo("ownsider");
        assertThat(writer.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(writer.lastLoginAt()).isEqualTo(1L);
    }

    @DisplayName("GET /api/posts/{id} 로 게시물 조회시 id 에 해당하는 게시물이 없으면 404 NotFound")
    @Test
    void getById_nonexistentId_404NotFound() throws Exception {
        // given
        long id = 3L;

        // when, then
        Assertions.assertThatThrownBy(() -> postController.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("posts 에서 id 3 을(를) 찾을 수 없습니다.");
    }

    @DisplayName("PUT /api/posts/{id} 요청으로 게시물을 수정할 수 있다")
    @Test
    void update_ok() throws Exception {
        // given
        long postId = 1L;
        PostUpdate dto = PostUpdate.builder()
                .content("내용 수정 테스트")
                .build();

        // when
        ResponseEntity<PostResponse> responseEntity = postController.update(postId, dto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        PostResponse body = responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.content()).isEqualTo(dto.getContent());
        assertThat(body.createdAt()).isEqualTo(1755812460000L);
        assertThat(body.modifiedAt()).isEqualTo(1234L);

        UserResponse writer = body.writer();
        assertThat(writer).isNotNull();
        assertThat(writer.id()).isEqualTo(2);
        assertThat(writer.email()).isEqualTo("ownsider@naver.com");
        assertThat(writer.nickname()).isEqualTo("ownsider");
        assertThat(writer.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(writer.lastLoginAt()).isEqualTo(1);
    }

    @DisplayName("PUT /api/posts/{id} 요청은 id 해당 게시물이 없으면 404 Not Found")
    @Test
    void update_nonexistentId_404NotFound() throws Exception {
        // given
        long postId = 3L;
        PostUpdate dto = PostUpdate.builder()
                .content("내용 수정 테스트")
                .build();

        assertThatThrownBy(() -> postController.update(postId, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("posts 에서 id 3 을(를) 찾을 수 없습니다.");
    }

}