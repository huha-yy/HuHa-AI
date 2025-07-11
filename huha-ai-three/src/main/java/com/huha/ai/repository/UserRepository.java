package com.huha.ai.repository;

import com.huha.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface UserRepository extends JpaRepository<User, Long> {
    // 可以添加自定义查询方法，例如根据用户名查找用户
    User findByUsername(String username);
} 