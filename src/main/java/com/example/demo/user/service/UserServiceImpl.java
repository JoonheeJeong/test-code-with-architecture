package com.example.demo.user.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.service.port.ClockProvider;
import com.example.demo.common.service.port.UUIDProvider;
import com.example.demo.user.controller.port.CertificationService;
import com.example.demo.user.controller.port.UserService;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final CertificationService certService;
    private final ClockProvider clockProvider;
    private final UUIDProvider uuidProvider;

    @Transactional(readOnly = true)
    public User getById(long id) {
        return userRepo.getById(id);
    }

    @Transactional(readOnly = true)
    public User getActiveByEmail(String email) {
        return userRepo.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("users", "email,status", email + ",ACTIVE"));
    }

    @Transactional(readOnly = true)
    public User getActiveById(long id) {
        return userRepo.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("users", "id,status", id + ",ACTIVE"));
    }

    @Transactional
    public User create(UserCreate userCreate) {
        User user = User.from(userCreate, uuidProvider.random());
        user = userRepo.save(user);
        certService.send(user.getId(), user.getCertificationCode(), user.getEmail());
        return user;
    }

    @Transactional
    public User update(long id, UserUpdate userUpdate) {
        User user = getActiveById(id);
        user.update(userUpdate);
        user = userRepo.save(user);
        return user;
    }

    @Transactional
    public void login(long id) {
        User user = getActiveById(id);
        user.login(clockProvider.nowMillis());
        userRepo.save(user);
    }

    @Transactional
    public void verifyEmail(long id, String certificationCode) {
        User user = getById(id);
        user.verify(certificationCode);
        userRepo.save(user);
    }

}
