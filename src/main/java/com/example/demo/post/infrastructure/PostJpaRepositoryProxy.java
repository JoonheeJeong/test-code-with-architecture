package com.example.demo.post.infrastructure;

import com.example.demo.post.domain.Post;
import com.example.demo.post.service.port.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostJpaRepositoryProxy implements PostRepository {

    private final PostJpaRepository jpaRepo;

    @Override
    public Optional<Post> findById(long id) {
        return jpaRepo.findById(id).map(PostEntity::toModel);
    }

    @Override
    public Post save(Post post) {
        PostEntity entity = PostEntity.from(post);
        entity = jpaRepo.save(entity);
        return entity.toModel();
    }
}