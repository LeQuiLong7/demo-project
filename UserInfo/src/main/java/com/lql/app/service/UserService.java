package com.lql.app.service;

import com.lql.app.model.User;
import com.lql.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(MultipartFile file, String userId, double balance) throws IOException {
        User user = new User(userId, file != null ? file.getBytes() : null, balance);
        return userRepository.save(user);
    }
    @CacheEvict(value = "user-avt", key = "#userId")
    public byte[] updateAvatar(MultipartFile file, String userId) throws IOException {
        User user = getUserById(userId);
        user.setAvatar(file.getBytes());
        userRepository.save(user);
        return user.getAvatar();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Cacheable(value = "user-avt", key = "#userId")
    public byte[] getAvatarById(String userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    System.out.println("Loi");
                    return new RuntimeException("avt-not-found");
                }).getAvatar();
    }

}
