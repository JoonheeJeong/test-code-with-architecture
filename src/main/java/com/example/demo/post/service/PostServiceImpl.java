package com.example.demo.post.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.service.port.ClockProvider;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final ClockProvider clockProvider;

    @Transactional(readOnly = true)
    public Post getById(long id) {
        return postRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", id));
    }

    @Transactional
    public Post create(PostCreate postCreate) {
        User user = userRepo.getActiveById(postCreate.getWriterId());
        Post post = Post.from(postCreate, user, clockProvider.nowMillis());
        return postRepo.save(post);
    }

    @Transactional
    public Post update(long id, PostUpdate postUpdate) {
        Post post = getById(id);
        post.update(postUpdate, clockProvider.nowMillis());
        return postRepo.save(post);
    }
}