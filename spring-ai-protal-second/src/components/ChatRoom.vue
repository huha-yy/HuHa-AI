<template>
  <div class="chat-room">
    <div class="chat-header">
      <h2>{{ roomName }}</h2>
      <div class="online-users">
        <div v-if="currentUserId" class="current-user-info">
          <div class="user-avatar">{{ placeholderAvatar }}</div>
          <span>{{ currentUserId }}</span>
        </div>
        <span v-for="user in onlineUsers" :key="user.id" class="user-badge">
          {{ user.name }}
        </span>
      </div>
    </div>

    <div class="messages" ref="messagesContainer">
      <div v-for="message in messages" :key="message.id" 
           :class="['message', message.senderId === currentUserId ? 'sent' : 'received']">
        <div class="message-header">
          <span class="sender">{{ message.senderName || message.senderId || '未知用户' }}</span>
          <span class="time">{{ formatTime(message.timestamp) }}</span>
        </div>
        <div class="message-content">{{ message.content }}</div>
      </div>
      <div v-if="isTyping" class="typing-indicator">
        {{ typingUser }} 正在输入...
      </div>
    </div>

    <!-- 临时显示 messages 数组内容 -->
    <!-- <p>Messages Array: {{ JSON.stringify(messages) }}</p> -->

    <div class="input-area">
      <textarea
        v-model="newMessage"
        @input="handleTyping"
        @keydown.enter.prevent="sendMessage"
        placeholder="输入消息..."
        rows="1"
      ></textarea>
      <button @click="sendMessage" :disabled="!newMessage.trim()">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue';
import { useRoute } from 'vue-router';
import { websocketService } from '../services/websocket';
import { chatAPI } from '../services/api';
import { format } from 'date-fns';
import { debounce } from '../utils/debounce';

const route = useRoute();
const roomId = route.params.roomId;
const currentUserId = history.state.currentUserId;
const roomName = route.params.roomName;

console.log("ChatRoom received userId:", currentUserId);

const messages = ref([]);
const newMessage = ref('');
const isTyping = ref(false);
const typingUser = ref('');
const messagesContainer = ref(null);
const onlineUsers = ref([]);

const placeholderAvatar = computed(() => {
  if (!currentUserId) return '';
  const name = String(currentUserId);
  if (name.length <= 2) {
    return name;
  } else {
    return name.slice(-2);
  }
});

const debouncedSendTypingStatus = debounce(() => {
  if (currentUserId && roomId) {
    websocketService.sendTypingStatus(roomId, true);
  }
}, 500);

let typingStopTimer = null;
const TYPING_STOP_DELAY = 1000;

// 获取历史消息
const fetchHistoryMessages = async () => {
  try {
    const historyMessages = await chatAPI.getRoomHistory(roomId);
    messages.value = historyMessages;
    scrollToBottom();
  } catch (error) {
    console.error("Failed to fetch history messages:", error);
  }
};

onMounted(() => {
  websocketService.connect(currentUserId);
  
  // 获取历史消息
  fetchHistoryMessages();

  websocketService.addMessageHandler('public', handlePublicMessage);
  websocketService.addMessageHandler('private', handlePrivateMessage);
  websocketService.addMessageHandler('typing', handleTypingStatus);
});

onUnmounted(() => {
  websocketService.removeMessageHandler('public', handlePublicMessage);
  websocketService.removeMessageHandler('private', handlePrivateMessage);
  websocketService.removeMessageHandler('typing', handleTypingStatus);
  websocketService.disconnect();
});

const handlePublicMessage = (message) => {
  console.log("Received public message:", message);
  if (!message.senderName) {
    message.senderName = message.senderId || '未知用户';
  }
  messages.value.push(message);
  console.log("Messages array after push:", messages.value);
  scrollToBottom();
};

const handlePrivateMessage = (message) => {
  if (message.roomId === roomId) {
    messages.value.push(message);
    scrollToBottom();
  }
};

const handleTypingStatus = (message) => {
  if (message.roomId === roomId) {
    isTyping.value = message.content;
    typingUser.value = message.senderName;
  }
};

const sendMessage = () => {
  if (!newMessage.value.trim()) return;

  const message = {
    id: Date.now().toString(),
    senderId: currentUserId,
    content: newMessage.value,
    timestamp: new Date(),
    roomId: roomId,
    type: 'CHAT'
  };

  websocketService.sendRoomMessage(roomId, message);
  newMessage.value = '';
  isTyping.value = false;
};

const handleTyping = () => {
  debouncedSendTypingStatus();

  if (typingStopTimer) {
    clearTimeout(typingStopTimer);
  }

  typingStopTimer = setTimeout(() => {
    if (currentUserId && roomId) {
      websocketService.sendTypingStatus(roomId, false);
    }
    typingStopTimer = null;
  }, TYPING_STOP_DELAY);
};

const scrollToBottom = async () => {
  await nextTick();
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

const formatTime = (timestamp) => {
  return format(new Date(timestamp), 'HH:mm');
};
</script>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f5f5f5;
}

.chat-header {
  padding: 1rem;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.online-users {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
  align-items: center;
}

.current-user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-right: 1rem;
  font-weight: bold;
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #42b883;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 0.8rem;
  flex-shrink: 0;
}

.user-badge {
  padding: 0.25rem 0.5rem;
  background: #e3f2fd;
  border-radius: 1rem;
  font-size: 0.875rem;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
}

.message {
  max-width: 70%;
  margin-bottom: 1rem;
  padding: 0.75rem;
  border-radius: 0.5rem;
}

.message.sent {
  margin-left: auto;
  background: #e3f2fd;
}

.message.received {
  margin-right: auto;
  background: #fff;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.25rem;
  font-size: 0.875rem;
  color: #666;
}

.message-content {
  word-break: break-word;
  color: #333;
}

.typing-indicator {
  padding: 0.5rem;
  color: #666;
  font-style: italic;
}

.input-area {
  padding: 1rem;
  background: #fff;
  border-top: 1px solid #eee;
  display: flex;
  gap: 0.5rem;
}

textarea {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 0.25rem;
  resize: none;
}

button {
  padding: 0.5rem 1rem;
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 0.25rem;
  cursor: pointer;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.message.sent .message-content {
  color: #333;
}

.message.received .message-content {
  color: #333;
}

/* 深色模式适配 */
.dark .chat-room {
  background: #181818;
}
.dark .chat-header {
  background: #232323;
  border-bottom: 1px solid #333;
}
.dark .messages {
  background: transparent;
}
.dark .message.sent {
  background: #1a365d;
  color: #fff;
}
.dark .message.received {
  background: #232323;
  color: #fff;
}
.dark .message-header {
  color: #aaa;
}
.dark .message-content {
  color: #fff !important;
  font-weight: 400;
}
.dark .message-header .sender {
  color: #fff !important;
  font-weight: 500;
}
.dark .typing-indicator {
  color: #aaa;
}
.dark .input-area {
  background: #232323;
  border-top: 1px solid #333;
}
.dark textarea {
  background: #181818;
  color: #fff;
  border: 1px solid #444;
}
.dark button {
  background: #1565c0;
  color: #fff;
}
.dark button:disabled {
  background: #444;
  color: #ccc;
}
.dark .user-avatar {
  background-color: #1565c0;
}
.dark .user-badge {
  background: #333;
  color: #fff;
}
</style> 