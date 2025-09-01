package com.example.demo.user.infrastructure;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserJpaRepositoryProxy implements UserRepository {

    private final UserJpaRepository jpaRepo;

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.from(user);
        entity = jpaRepo.save(entity);
        return entity.toModel();
    }

    @Override
    public Optional<User> findById(long id) {
        return jpaRepo.findById(id).map(UserEntity::toModel);
    }

    @Override
    public Optional<User> findByEmailAndStatus(String email, UserStatus userStatus) {
        return jpaRepo.findByEmailAndStatus(email, userStatus).map(UserEntity::toModel);
    }

    @Override
    public Optional<User> findByIdAndStatus(long id, UserStatus userStatus) {
        return jpaRepo.findByIdAndStatus(id, userStatus).map(UserEntity::toModel);
    }

    @Override
    public User getActiveById(long id) {
        return findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("users", "id", id));
    }
}
