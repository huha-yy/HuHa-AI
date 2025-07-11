package com.huha.ai.service.impl;

import com.huha.ai.service.UserService;
import com.huha.ai.entity.User;
import com.huha.ai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // 开启事务
    public User findOrCreateUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            // 可以生成默认头像或其他信息
            user.setAvatar(generatePlaceholderAvatar(username)); 
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // 简单的占位符头像生成方法
    private String generatePlaceholderAvatar(String username) {
        if (username == null || username.length() < 2) {
            return username;
        } else {
            return username.substring(username.length() - 2);
        }
    }
} 