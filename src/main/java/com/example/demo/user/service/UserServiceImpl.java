package com.example.demo.user.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
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

    private final UserRepository userRepository;
    private final CertificationService certificationService;

    @Transactional(readOnly = true)
    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    @Transactional(readOnly = true)
    public User getActiveByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Users", email));
    }

    @Transactional(readOnly = true)
    public User getActiveById(long id) {
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    @Transactional
    public User create(UserCreate userCreate) {
        User user = User.from(userCreate);
        user = userRepository.save(user);
        certificationService.send(user.getId(), user.getCertificationCode(), user.getEmail());
        return user;
    }

    @Transactional
    public User update(long id, UserUpdate userUpdate) {
        User user = getActiveById(id);
        user.update(userUpdate);
        user = userRepository.save(user);
        return user;
    }

    @Transactional
    public void login(long id) {
        User user = getById(id);
        user.login();
        userRepository.save(user);
    }

    @Transactional
    public void verifyEmail(long id, String certificationCode) {
        User user = getById(id);
        user.verify(certificationCode);
        userRepository.save(user);
    }

}
