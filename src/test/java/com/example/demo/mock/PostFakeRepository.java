package com.example.demo.mock;

import com.example.demo.post.domain.Post;
import com.example.demo.post.service.port.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostFakeRepository implements PostRepository {

    private long autoIncrementId = 0L;
    private final List<Post> data = new ArrayList<>();

    @Override
    public Post save(Post post) {
        long postId = (post.getId() != null && post.getId() > 0) ? post.getId() : ++autoIncrementId;
        if (postId > autoIncrementId) {
            autoIncrementId = postId;
        }
        int i = 0;
        for (; i < data.size(); i++) {
            if (data.get(i).getId().equals(post.getId())) {
                break;
            }
        }
        if (i < data.size()) {
            data.remove(i);
        }
        Post savePost = Post.builder()
                .id(postId)
                .content(post.getContent())
                .writer(post.getWriter())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
        data.add(i, savePost);
        return savePost;
    }

    @Override
    public Optional<Post> findById(long id) {
        return data.stream()
                .filter(findPost -> findPost.getId().equals(id))
                .findAny();
    }

}
