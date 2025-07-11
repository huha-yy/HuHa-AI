package com.huha.ai.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ChatMessage {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
    private String roomId;  // 群聊房间ID

    public enum MessageType {
        CHAT,       // 普通消息
        JOIN,       // 加入房间
        LEAVE,      // 离开房间
        TYPING,     // 正在输入
        READ        // 已读回执
    }

    public ChatMessage() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage(String senderId, String content, MessageType type) {
        this();
        this.senderId = senderId;
        this.content = content;
        this.type = type;
    }

    public ChatMessage(String senderId, String content, String type) {
        this(senderId, content, MessageType.valueOf(type));
    }
} 