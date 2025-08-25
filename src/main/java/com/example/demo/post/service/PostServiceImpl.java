package com.example.demo.post.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserServiceImpl userService;

    @Transactional(readOnly = true)
    public Post getById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", id));
    }

    @Transactional
    public Post create(PostCreate postCreate) {
        User user = userService.getActiveById(postCreate.getWriterId());
        Post post = Post.from(postCreate, user);
        return postRepository.save(post);
    }

    @Transactional
    public Post update(long id, PostUpdate postUpdate) {
        Post post = getById(id);
        post.update(postUpdate);
        return postRepository.save(post);
    }
}