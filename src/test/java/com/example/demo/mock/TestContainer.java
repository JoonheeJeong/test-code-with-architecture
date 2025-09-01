package com.example.demo.mock;

import com.example.demo.common.infrastructure.SystemClockProvider;
import com.example.demo.common.infrastructure.SystemUUIDProvider;
import com.example.demo.common.service.port.ClockProvider;
import com.example.demo.common.service.port.UUIDProvider;
import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.service.PostServiceImpl;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.controller.port.CertificationService;
import com.example.demo.user.controller.port.UserService;
import com.example.demo.user.service.CertificationServiceImpl;
import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.MailSender;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;

public class TestContainer {

    public final PostRepository postRepo;
    public final UserRepository userRepo;

    public final PostService postService;
    public final UserService userService;

    @Builder
    public TestContainer(ClockProvider clockProvider) {
        this.postRepo = new PostFakeRepository();
        this.userRepo = new UserFakeRepository();

        this.postService = new PostServiceImpl(postRepo, userRepo, clockProvider);

        MailSender mailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationServiceImpl(mailSender);
        UUIDProvider uuidProvider = new SystemUUIDProvider();
        this.userService = new UserServiceImpl(userRepo, certificationService, clockProvider, uuidProvider);
    }
}
