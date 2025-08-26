package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Post {
    private final Long id;
    private String content;
    private final Long createdAt;
    private Long modifiedAt;
    private final User writer;

    @Builder
    public Post(Long id, String content, Long createdAt, Long modifiedAt, User writer) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.writer = writer;
    }

    public static Post from(PostCreate postCreate, User user, long createdAt) {
        return builder()
                .writer(user)
                .content(postCreate.getContent())
                .createdAt(createdAt)
                .build();
    }

    public void update(PostUpdate postUpdate, long modifiedAt) {
        this.content = postUpdate.getContent();
        this.modifiedAt = modifiedAt;
    }

}
