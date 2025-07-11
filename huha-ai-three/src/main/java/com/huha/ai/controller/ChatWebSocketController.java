package com.huha.ai.controller;

import com.huha.ai.entity.ChatMessage;
import com.huha.ai.entity.User;
import com.huha.ai.repository.ChatMessageRepository;
import com.huha.ai.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ChatWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate, UserService userService, ChatMessageRepository chatMessageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.chatMessageRepository = chatMessageRepository;
    }

    // 处理 STOMP CONNECT 事件，用户连接成功
    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        logger.info("WebSocket CONNECT event triggered. Session ID: {}", headerAccessor.getSessionId());
        logger.info("Session Attributes: {}", headerAccessor.getSessionAttributes());

        String username = (String) headerAccessor.getSessionAttributes().get("username"); // 假设用户名存储在 session attributes 中
        
        if (username != null) {
            User user = userService.findOrCreateUser(username);
            headerAccessor.getSessionAttributes().put("userId", user.getId());
            logger.info("User connected: {} with ID: {}", username, user.getId());
        } else {
            logger.warn("User connected without username in session attributes. Session ID: {}", headerAccessor.getSessionId());
        }
    }

    @MessageMapping("/chat.room")
    @SendTo("/topic/public")
    public ChatMessage handleRoomMessage(
            @Payload ChatMessage message,
            SimpMessageHeaderAccessor headerAccessor,
            @Header(name = "userId", required = false) String userIdHeader) {

        String senderId = userIdHeader;
        if (senderId == null) {
            senderId = "unknown";
        }

        ChatMessage savedMessage = new ChatMessage();
        savedMessage.setContent(message.getContent());
        savedMessage.setRoomId(message.getRoomId());
        savedMessage.setType(message.getType());
        savedMessage.setReceiverId(message.getReceiverId());
        savedMessage.setSenderId(senderId);

        // 查 nickname
        try {
            Long senderUserIdLong = Long.valueOf(senderId);
            User user = userService.findById(senderUserIdLong);
            if (user != null) {
                savedMessage.setSenderName(user.getUsername());
            } else {
                savedMessage.setSenderName("未知用户");
            }
        } catch (Exception e) {
            savedMessage.setSenderName("未知用户");
        }

        logger.info("Received room message: {}", savedMessage);
        ChatMessage resultMessage = chatMessageRepository.save(savedMessage);
        return resultMessage;
    }

    @MessageMapping("/chat.private")
    public void handlePrivateMessage(
            @Payload ChatMessage message,
            SimpMessageHeaderAccessor headerAccessor,
            @Header(name = "userId", required = false) String userIdHeader) {

        String senderId = userIdHeader;
        if (senderId == null) {
            senderId = "unknown";
        }

        ChatMessage savedMessage = new ChatMessage();
        savedMessage.setContent(message.getContent());
        savedMessage.setRoomId(message.getRoomId());
        savedMessage.setType(message.getType());
        savedMessage.setReceiverId(message.getReceiverId());
        savedMessage.setSenderId(senderId);

        // 查 nickname
        try {
            Long senderUserIdLong = Long.valueOf(senderId);
            User user = userService.findById(senderUserIdLong);
            if (user != null) {
                savedMessage.setSenderName(user.getUsername());
            } else {
                savedMessage.setSenderName("未知用户");
            }
        } catch (Exception e) {
            savedMessage.setSenderName("未知用户");
        }

        logger.info("Received private message: {}", savedMessage);
        ChatMessage resultMessage = chatMessageRepository.save(savedMessage);

        messagingTemplate.convertAndSendToUser(
            resultMessage.getReceiverId(),
            "/queue/private",
            resultMessage
        );
    }

    @MessageMapping("/chat.typing")
    public void handleTypingStatus(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        Long senderUserId = (Long) headerAccessor.getSessionAttributes().get("userId");
        if (senderUserId != null) {
            message.setSenderId(String.valueOf(senderUserId));
        }
        logger.info("Received typing status: {}", message);

        if (message.getReceiverId() != null) {
            // 私聊输入状态，点对点发送
            messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/typing",
                message
            );
        } else {
            // 群聊输入状态，广播到所有人
            messagingTemplate.convertAndSend(
                "/topic/typing",
                message
            );
            logger.info("Typing status broadcasted to /topic/typing");
        }
    }

    // 添加获取历史消息的 HTTP 端点
    @GetMapping("/history/room/{roomId}")
    @ResponseBody
    public List<ChatMessage> getRoomHistory(@PathVariable String roomId) {
        logger.info("Fetching history for room: {}", roomId);
        // 根据 roomId 从数据库查找历史消息，并按时间戳排序
        // 需要在 ChatMessageRepository 中添加查询方法
        // 例如：List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    // 处理 STOMP DISCONNECT 事件，用户断开连接
    // @EventListener
    // public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    //     SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
    //     Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
    //     if (userId != null) {
    //         logger.info("User disconnected: ID {}", userId);
    //         // 可以处理用户下线逻辑，例如更新在线用户列表
    //     }
    // }
} 