package com.example.demo.mock;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserFakeRepository implements UserRepository {

    private long autoIncrementId = 0L;
    private final List<User> data = new ArrayList<>();

    @Override
    public User save(User user) {
        long userId = (user.getId() != null && user.getId() > 0) ? user.getId() : ++autoIncrementId;
        if (userId > autoIncrementId) {
            autoIncrementId = userId;
        }
        int i = 0;
        for (; i < data.size(); i++) {
            if (data.get(i).getId().equals(user.getId())) {
                break;
            }
        }
        if (i < data.size()) {
            data.remove(i);
        }
        User saveUser = User.builder()
                .id(userId)
                .email(user.getEmail())
                .status(user.getStatus())
                .certificationCode(user.getCertificationCode())
                .lastLoginAt(user.getLastLoginAt())
                .nickname(user.getNickname())
                .address(user.getAddress())
                .build();
        data.add(i, saveUser);
        return saveUser;
    }

    @Override
    public Optional<User> findById(long id) {
        return data.stream()
                .filter(findUser -> findUser.getId().equals(id))
                .findAny();
    }

    @Override
    public Optional<User> findByEmailAndStatus(String email, UserStatus userStatus) {
        return data.stream()
                .filter(findUser -> findUser.getEmail().equals(email) && findUser.getStatus().equals(userStatus))
                .findAny();
    }

    @Override
    public Optional<User> findByIdAndStatus(long id, UserStatus userStatus) {
        return data.stream()
                .filter(findUser -> findUser.getId().equals(id) && findUser.getStatus().equals(userStatus))
                .findAny();
    }

}
