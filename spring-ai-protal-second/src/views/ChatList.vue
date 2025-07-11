<template>
  <div class="chat-list">
    <div v-if="!isLoggedIn" class="login-form">
      <h2>请输入您的昵称</h2>
      <div class="input-group">
        <input 
          v-model="username" 
          @keyup.enter="handleLogin"
          placeholder="请输入昵称"
          maxlength="20"
          :disabled="isLoading"
        />
        <button 
          @click="handleLogin" 
          :disabled="!username.trim() || isLoading"
          class="login-btn"
        >
          {{ isLoading ? '登录中...' : '进入聊天室' }}
        </button>
      </div>
      <p v-if="error" class="error-message">{{ error }}</p>
    </div>
    <div v-else>
      <div class="header">
        <h1>聊天室列表</h1>
        <div class="user-info">
          <span class="username">{{ currentUser.username }}</span>
          <button @click="handleLogout" class="logout-btn">退出</button>
        </div>
      </div>
      
      <div v-if="isLoading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载聊天室中...</p>
      </div>
      
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button @click="fetchChatRooms" class="retry-btn">重试</button>
      </div>
      
      <div v-else class="room-list">
        <div v-for="room in chatRooms" 
             :key="room.id" 
             class="room-item" 
             @click="joinRoom(room)"
             :class="{ 'room-item--loading': joiningRoom === room.id }"
        >
          <div class="room-info">
            <h3>{{ room.name }}</h3>
            <p>{{ room.description }}</p>
            <div class="room-stats">
              <span class="online-count">{{ room.onlineUsers }} 人在线</span>
              <span class="room-type">{{ room.type || '公共' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { authService } from '../services/auth';

const router = useRouter();
const username = ref('');
const isLoading = ref(false);
const error = ref('');
const joiningRoom = ref(null);
const chatRooms = ref([]);

const isLoggedIn = computed(() => authService.isLoggedIn());
const currentUser = computed(() => authService.getCurrentUser());

// 获取聊天室列表
const fetchChatRooms = async () => {
  isLoading.value = true;
  error.value = '';
  try {
    // TODO: 替换为实际的API调用
    // const response = await chatAPI.getChatRooms();
    // chatRooms.value = response.data;
    
    // 临时使用模拟数据
    chatRooms.value = [
      {
        id: 'general',
        name: '公共聊天室',
        description: '欢迎来到公共聊天室，这里可以自由交流',
        onlineUsers: Math.floor(Math.random() * 50),
        type: '公共'
      },
      {
        id: 'tech',
        name: '技术交流',
        description: '技术讨论、问题解答、经验分享',
        onlineUsers: Math.floor(Math.random() * 30),
        type: '技术'
      },
      {
        id: 'game',
        name: '游戏交流',
        description: '游戏攻略、组队、闲聊',
        onlineUsers: Math.floor(Math.random() * 40),
        type: '游戏'
      }
    ];
  } catch (err) {
    error.value = '获取聊天室列表失败，请稍后重试';
    console.error('Failed to fetch chat rooms:', err);
  } finally {
    isLoading.value = false;
  }
};

// 登录处理
const handleLogin = async () => {
  if (!username.value.trim()) return;
  
  isLoading.value = true;
  error.value = '';
  try {
    await authService.login(username.value.trim());
    username.value = '';
    await fetchChatRooms();
  } catch (err) {
    error.value = '登录失败，请重试';
    console.error('Login failed:', err);
  } finally {
    isLoading.value = false;
  }
};

// 登出处理
const handleLogout = () => {
  authService.logout();
  router.push('/');
};

// 加入聊天室
const joinRoom = async (room) => {
  if (joiningRoom.value) return;
  
  joiningRoom.value = room.id;
  try {
    const currentUser = authService.getCurrentUser();
    if (!currentUser?.id) {
      error.value = '请先登录';
      router.push('/');
      return;
    }

    router.push({
      name: 'ChatRoom',
      params: { roomId: room.id },
      state: { currentUserId: currentUser.id }
    });
  } catch (err) {
    error.value = '加入聊天室失败，请重试';
    console.error('Failed to join room:', err);
  } finally {
    joiningRoom.value = null;
  }
};

onMounted(() => {
  if (isLoggedIn.value) {
    fetchChatRooms();
  }
});
</script>

<style>
/* 浅色模式（默认） */
.room-item, .login-form {
  background: #fff;
  color: #222;
}
.room-info h3,
.room-info p,
.room-stats,
.room-type,
.username {
  color: #222;
}

/* 深色模式 */
.dark .room-item,
.dark .login-form {
  background: #232323;
  color: #fff;
}
.dark .room-info h3,
.dark .room-info p,
.dark .room-stats,
.dark .room-type,
.dark .username {
  color: #fff;
}

.chat-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logout-btn {
  padding: 0.5rem 1rem;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background: #ff7875;
}

.login-form {
  max-width: 400px;
  margin: 4rem auto;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.login-form h2 {
  color: #222;
}

.input-group {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid var(--border-color, #ddd);
  border-radius: 4px;
  background: #fff;
  color: #222;
}

input:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.1);
}

button {
  padding: 0.5rem 1rem;
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover:not(:disabled) {
  background: #1565c0;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.login-btn {
  min-width: 100px;
}

.error-message {
  color: #ff4d4f;
  margin-top: 1rem;
  text-align: center;
}

.loading-state,
.error-state {
  text-align: center;
  padding: 2rem;
  color: var(--text-color, #666);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 1rem;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #1976d2;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.retry-btn {
  margin-top: 1rem;
  background: #1976d2;
}

.room-list {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
}

.room-item {
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.room-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.room-item--loading {
  opacity: 0.7;
  cursor: wait;
}

.room-item--loading::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: #1976d2;
  animation: loading 1s infinite linear;
}

.room-info h3 {
  margin: 0;
  font-size: 1.2rem;
}

.room-info p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.room-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
  font-size: 0.875rem;
  color: var(--text-secondary, #888);
}

.online-count {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.online-count::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #52c41a;
  border-radius: 50%;
}

.room-type {
  padding: 0.25rem 0.5rem;
  background: rgba(25, 118, 210, 0.1);
  border-radius: 4px;
  color: #1976d2;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes loading {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* 响应式布局 */
@media (max-width: 768px) {
  .chat-list {
    padding: 1rem;
  }
  
  .header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .room-list {
    grid-template-columns: 1fr;
  }
  
  .input-group {
    flex-direction: column;
  }
  
  button {
    width: 100%;
  }
}

.dark .login-form h2 {
  color: #fff;
}
</style> 