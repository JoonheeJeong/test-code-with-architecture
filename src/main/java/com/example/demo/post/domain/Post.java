package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.Clock;

@Getter
public class Post {
    private Long id;
    private String content;
    private Long createdAt;
    private Long modifiedAt;
    private User writer;

    @Builder
    public Post(Long id, String content, Long createdAt, Long modifiedAt, User writer) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.writer = writer;
    }

    public static Post from(PostCreate postCreate, User user) {
        return builder()
                .writer(user)
                .content(postCreate.getContent())
                .createdAt(Clock.systemUTC().millis())
                .build();
    }

    public void update(PostUpdate postUpdate) {
        this.content = postUpdate.getContent();
        this.modifiedAt = Clock.systemUTC().millis();
    }

}
