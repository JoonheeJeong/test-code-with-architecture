package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.user.controller.response.UserResponse;
import lombok.AccessLevel;
import lombok.Builder;

public record PostResponse(
        Long id,
        String content,
        Long createdAt,
        Long modifiedAt,
        UserResponse writer
) {

    @Builder(access = AccessLevel.PRIVATE)
    public PostResponse {
    }

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .writer(UserResponse.from(post.getWriter()))
                .build();
    }

}
