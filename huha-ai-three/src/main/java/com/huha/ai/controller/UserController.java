package com.huha.ai.controller;

import com.huha.ai.service.UserService;
import com.huha.ai.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User loginUser, HttpSession session) {
        if (loginUser == null || loginUser.getUsername() == null || loginUser.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String username = loginUser.getUsername().trim();
        logger.info("Attempting to login/create user: {}", username);
        User user = userService.findOrCreateUser(username);
        session.setAttribute("username", username);
        logger.info("User logged in/created: {} with ID: {}", user.getUsername(), user.getId());
        return ResponseEntity.ok(user);
    }

    // 可以添加其他用户相关的接口，例如获取用户信息等
} 