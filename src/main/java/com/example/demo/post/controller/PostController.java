package com.example.demo.post.controller;

import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.PostService;
import com.example.demo.user.controller.UserController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시물(posts)")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserController userController;

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(toResponse(postService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable long id, @RequestBody PostUpdate postUpdate) {
        return ResponseEntity
                .ok()
                .body(toResponse(postService.update(id, postUpdate)));
    }

    public PostResponse toResponse(Post post) {
        PostResponse PostResponse = new PostResponse();
        PostResponse.setId(post.getId());
        PostResponse.setContent(post.getContent());
        PostResponse.setCreatedAt(post.getCreatedAt());
        PostResponse.setModifiedAt(post.getModifiedAt());
        PostResponse.setWriter(userController.toResponse(post.getWriter()));
        return PostResponse;
    }
}