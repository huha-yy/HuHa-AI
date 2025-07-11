package com.huha.ai.repository;

import com.huha.ai.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 根据 roomId 查找消息并按时间戳排序
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
} 