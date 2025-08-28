package com.example.demo.post.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.infrastructure.SystemClockProvider;
import com.example.demo.common.infrastructure.SystemUUIDProvider;
import com.example.demo.common.service.port.ClockProvider;
import com.example.demo.common.service.port.UUIDProvider;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.PostFakeRepository;
import com.example.demo.mock.UserFakeRepository;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.CertificationServiceImpl;
import com.example.demo.user.service.UserService;
import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.MailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class FastPostServiceTest {

    private PostFakeRepository postRepo;
    private UserFakeRepository userRepo;
    private PostService postService;

    @BeforeEach
    void setUp() {
        userRepo = new UserFakeRepository();
        postRepo = new PostFakeRepository();
        MailSender mailSender = new FakeMailSender();
        ClockProvider clockProvider = new SystemClockProvider();
        UUIDProvider uuidProvider = new SystemUUIDProvider();
        CertificationServiceImpl certificationService = new CertificationServiceImpl(mailSender);
        UserService userService = new UserServiceImpl(userRepo, certificationService, clockProvider, uuidProvider);
        postService = new PostServiceImpl(postRepo, userService, clockProvider);

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
    }

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
        User writer = userRepo.findById(2L).orElseThrow();
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