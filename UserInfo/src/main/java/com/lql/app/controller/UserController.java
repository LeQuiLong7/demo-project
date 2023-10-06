package com.lql.app.controller;


import com.lql.app.dto.UserDTO;
import com.lql.app.model.User;
import com.lql.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @KafkaListener(topics = "create_account")
    public void listen(ConsumerRecord<Object, String> record) throws IOException {
        userService.createUser(null, record.value(), 100);
        log.info("account {} created successfully", record.value());
    }

    @PostMapping("/avt")
    public ResponseEntity<byte[]> updateAvatar(MultipartFile file, Authentication authentication) throws IOException {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                .body(userService.updateAvatar(file, authentication.getCredentials().toString()));
    }


    @GetMapping("/avt")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<byte[]> getAvatar(Authentication authentication) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                .body(userService.getAvatarById(authentication.getCredentials().toString()));
    }
    @GetMapping
    public UserDTO getInfo(Authentication authentication) {
        String userId = authentication.getCredentials().toString();
        UserDTO userDTO = new UserDTO(userId, authentication.getName(), userService.getUserById(userId).getBalance());
        System.out.println(userDTO);
        return userDTO;
    }


}
