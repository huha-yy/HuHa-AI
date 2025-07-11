import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';
import { authService } from './auth';

// 从环境变量获取后端 WebSocket URL
const backendWsUrl = import.meta.env.VITE_BACKEND_WS_URL;

export const websocketService = {
  stompClient: null,
  messageHandlers: {},
  errorHandler: null,
  connected: false,

  connect(userId) {
    if (this.connected && this.stompClient && this.stompClient.connected) {
      console.log('WebSocket already connected.');
      return;
    }

    console.log("Connecting to WebSocket with URL:", backendWsUrl);
    const socket = new SockJS(backendWsUrl);
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect({
      userId: userId // 在 CONNECT 帧中发送 userId
    }, frame => {
      console.log('Connected: ' + frame);
      this.connected = true;
      // 订阅公共聊天室
      this.stompClient.subscribe('/topic/public', message => {
        const chatMessage = JSON.parse(message.body);
        console.log("Received message from /topic/public:", chatMessage);
        this.handleMessage('public', chatMessage);
      });

      // 订阅私人消息 (使用用户自己的 queue)
      this.stompClient.subscribe('/user/queue/private', message => {
        const chatMessage = JSON.parse(message.body);
        console.log("Received message from /user/queue/private:", chatMessage);
        this.handleMessage('private', chatMessage);
      });

      // 订阅输入状态
      this.stompClient.subscribe('/user/queue/typing', message => {
        const typingStatus = JSON.parse(message.body);
        console.log("Received typing status from /user/queue/typing:", typingStatus);
        this.handleMessage('typing', typingStatus);
      });

      // 通知后端用户加入
      this.stompClient.send("/app/chat.addUser", JSON.stringify({ senderId: userId, type: 'JOIN' }), {});

    }, error => {
      console.error('WebSocket connection error:', error);
      this.connected = false;
      if (this.errorHandler) {
        this.errorHandler(error);
      }
      // 考虑重连逻辑
      setTimeout(() => { this.connect(userId); }, 5000);
    });
  },

  disconnect() {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.disconnect(() => {
        console.log("Disconnected");
        this.connected = false;
      });
    }
  },

  // 通用消息处理函数
  handleMessage(type, message) {
    if (this.messageHandlers[type]) {
      this.messageHandlers[type](message);
    }
  },

  // 添加消息处理器
  addMessageHandler(type, handler) {
    this.messageHandlers[type] = handler;
  },

  // 移除消息处理器
  removeMessageHandler(type, handler) {
    if (this.messageHandlers[type] === handler) {
      delete this.messageHandlers[type];
    }
  },

  // 发送公共房间消息
  sendRoomMessage(roomId, message) {
    if (this.stompClient && this.stompClient.connected) {
       // 在 SEND 帧中添加 userId header
      this.stompClient.send(
        `/app/chat.room`,
        JSON.stringify(message),
        { userId: message.senderId } // 在这里添加 userId header
        );
      console.log("Sent room message to /app/chat.room:", message);
    } else {
      console.warn("WebSocket not connected. Cannot send room message.");
    }
  },

  // 发送私人消息
  sendPrivateMessage(receiverId, message) {
     if (this.stompClient && this.stompClient.connected) {
         // 在 SEND 帧中添加 userId header
        this.stompClient.send(
          `/app/chat.private`,
          JSON.stringify(message),
          { userId: message.senderId } // 在这里添加 userId header
          );
         console.log("Sent private message to /app/chat.private:", message);
     } else {
         console.warn("WebSocket not connected. Cannot send private message.");
     }
  },

   // 发送输入状态
   sendTypingStatus(roomId, isTyping) {
     if (this.stompClient && this.stompClient.connected) {
      const currentUser = authService.getCurrentUser();
      if (!currentUser) {
        console.warn("No user logged in. Cannot send typing status.");
        return;
      }

      const message = { 
        senderId: currentUser.id, 
        roomId: roomId, 
        content: isTyping, 
        type: 'TYPING' 
      };

      // 在 SEND 帧中添加 userId header
      this.stompClient.send(
        `/app/chat.typing`,
        JSON.stringify(message),
        { userId: currentUser.id }
      );
      console.log("Sent typing status to /app/chat.typing:", message);
     } else {
        console.warn("WebSocket not connected. Cannot send typing status.");
     }
   },

  // 设置错误处理器
  setErrorHandler(handler) {
    this.errorHandler = handler;
  },
}; 