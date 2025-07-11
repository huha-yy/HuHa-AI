package com.huha.ai.service;

import com.huha.ai.entity.User;

public interface UserService {
    /**
     * 根据用户名查找用户，如果用户不存在则创建新用户并保存。
     *
     * @param username 用户名
     * @return 查找或创建的用户对象
     */
    User findOrCreateUser(String username);

    /**
     * 根据用户 ID 查找用户。
     *
     * @param userId 用户 ID
     * @return 用户对象，如果不存在则返回 null
     */
    User findById(Long userId);
} 