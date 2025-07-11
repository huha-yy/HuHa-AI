<template>
  <div class="consultant-view" :class="{ 'dark': isDark }">
    <div class="container">
      <div class="chat-container">
        <div class="chat-header">
          <h1>AI志愿填报顾问</h1>
          <p>专业指导，让高考志愿填报更轻松</p>
          <button @click="startNewConversation" class="new-chat-btn">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-5 h-5 mr-1">
              <path fill-rule="evenodd" d="M12 3.75a.75.75 0 01.75.75v6.75h6.75a.75.75 0 010 1.5h-6.75v6.75a.75.75 0 01-1.5 0v-6.75H4.5a.75.75 0 010-1.5h6.75V4.5a.75.75 0 01.75-.75z" clip-rule="evenodd" />
            </svg>
            新对话
          </button>
        </div>
        
        <div class="chat-messages" ref="chatMessages">
          <div v-for="(message, index) in messages" 
               :key="index" 
               :class="['message', message.role]">
            <div class="message-content">
              <div v-if="message.role === 'assistant'" class="avatar">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M4.5 6.375a4.125 4.125 0 118.25 0 4.125 4.125 0 01-8.25 0zm9.75 0a4.125 4.125 0 118.25 0 4.125 4.125 0 01-8.25 0zM1.5 19.125a7.125 7.125 0 0114.25 0v.003l-.001.119a.75.75 0 01-.363.63 13.067 13.067 0 01-6.761 1.873c-2.472 0-4.786-.684-6.76-1.873a.75.75 0 01-.364-.63l-.001-.122zM17.25 19.128l-.001.144a2.25 2.25 0 01-.233.96 10.088 10.088 0 005.06-1.01.75.75 0 00.42-.643 4.875 4.875 0 00-6.957-4.611 8.586 8.586 0 011.71 5.157v.003z" />
                </svg>
              </div>
              <div v-if="message.role === 'user'" class="avatar user">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path fill-rule="evenodd" d="M7.5 6a4.5 4.5 0 119 0 4.5 4.5 0 01-9 0zM3.751 20.105a8.25 8.25 0 0116.498 0 .75.75 0 01-.437.695A18.683 18.683 0 0112 22.5c-2.786 0-5.433-.608-7.812-1.7a.75.75 0 01-.437-.695z" clip-rule="evenodd" />
                </svg>
              </div>
              <div class="text" v-html="formatMessage(message.content)"></div>
              <span v-if="message.isStreaming" class="typing-cursor"></span>
            </div>
          </div>
          <div v-if="isTyping" class="message assistant">
            <div class="message-content">
              <div class="avatar">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M4.5 6.375a4.125 4.125 0 118.25 0 4.125 4.125 0 01-8.25 0zm9.75 0a4.125 4.125 0 118.25 0 4.125 4.125 0 01-8.25 0zM1.5 19.125a7.125 7.125 0 0114.25 0v.003l-.001.119a.75.75 0 01-.363.63 13.067 13.067 0 01-6.761 1.873c-2.472 0-4.786-.684-6.76-1.873a.75.75 0 01-.364-.63l-.001-.122zM17.25 19.128l-.001.144a2.25 2.25 0 01-.233.96 10.088 10.088 0 005.06-1.01.75.75 0 00.42-.643 4.875 4.875 0 00-6.957-4.611 8.586 8.586 0 011.71 5.157v.003z" />
                </svg>
              </div>
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="chat-input">
          <textarea 
            ref="messageInput"
            v-model="newMessage" 
            @keydown.enter.prevent="sendMessage"
            placeholder="请输入您的高考志愿填报问题..." 
            rows="1"
          ></textarea>
          <button @click="sendMessage" :disabled="!newMessage.trim()">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6">
              <path d="M3.478 2.405a.75.75 0 00-.926.94l2.432 7.905H13.5a.75.75 0 010 1.5H4.984l-2.432 7.905a.75.75 0 00.926.94 60.519 60.519 0 0018.445-8.986.75.75 0 000-1.218A60.517 60.517 0 003.478 2.405z" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch, computed } from 'vue'
import { useDark } from '@vueuse/core'
import axios from 'axios'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const isDark = useDark()
const chatMessages = ref(null)
const messageInput = ref(null)
const newMessage = ref('')
const messages = ref([
  {
    role: 'assistant',
    content: '你好！我是传智教育提供的AI志愿填报顾问，可以帮您：\n\n' +
             '1. 查询目标院校信息（简介、录取规则、奖学金、食宿条件等）\n' +
             '2. 查询2024年专业录取情况\n' +
             '3. 了解热门专业和就业前景\n' +
             '4. 根据您的分数推荐合适的学校和专业\n' +
             '5. 预约一对一志愿填报指导服务\n\n' +
             '请问有什么可以帮您的吗？',
    isStreaming: false
  }
])
const isTyping = ref(false)
const isStreaming = ref(false)
const streamedContent = ref('')
const typingSpeed = ref(20) // 打字速度(ms)
let typingInterval = null
const memoryId = ref(generateUUID())

// 格式化消息，支持markdown
const formatMessage = (content) => {
  const sanitizedContent = DOMPurify.sanitize(marked(content))
  return sanitizedContent
}

// 生成UUID作为会话ID
function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

// 新建会话
const startNewConversation = () => {
  // 生成新的会话ID
  memoryId.value = generateUUID()
  
  // 清空消息列表，只保留欢迎消息
  messages.value = [{
    role: 'assistant',
    content: '你好！我是传智教育提供的AI志愿填报顾问，可以帮您：\n\n' +
             '1. 查询目标院校信息（简介、录取规则、奖学金、食宿条件等）\n' +
             '2. 查询2024年专业录取情况\n' +
             '3. 了解热门专业和就业前景\n' +
             '4. 根据您的分数推荐合适的学校和专业\n' +
             '5. 预约一对一志愿填报指导服务\n\n' +
             '请问有什么可以帮您的吗？'
  }]
  
  // 清空输入框
  newMessage.value = ''
  
  // 滚动到底部
  nextTick(() => {
    scrollToBottom()
    // 聚焦输入框
    if (messageInput.value) {
      messageInput.value.focus()
    }
  })
}

// 自动调整文本域高度
const autoResizeTextarea = () => {
  const textarea = messageInput.value
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = textarea.scrollHeight + 'px'
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  }
}

// 发送消息
const sendMessage = async () => {
  const message = newMessage.value.trim()
  if (!message) return
  
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: message
  })
  
  // 清空输入框并重置高度
  newMessage.value = ''
  if (messageInput.value) {
    messageInput.value.style.height = 'auto'
  }
  
  // 滚动到底部
  await scrollToBottom()
  
  // 显示打字指示器
  isTyping.value = true
  
  let response = null
  try {
    console.log(`发送请求到: /consultant-api/chat?memoryId=${memoryId.value}&message=${encodeURIComponent(message)}`)
    // 使用代理访问consultant项目的API接口
    response = await fetch(`/consultant-api/chat?memoryId=${memoryId.value}&message=${encodeURIComponent(message)}`, {
      method: 'GET',
      headers: {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'
      }
    })
    
    if (!response.body) {
      throw new Error('ReadableStream not supported in this browser.')
    }
    
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let assistantMessage = ''
    
    // 清除之前的打字效果
    if (typingInterval) {
      clearInterval(typingInterval)
      typingInterval = null
    }
    
    // 创建AI回复消息
    messages.value.push({
      role: 'assistant',
      content: '',
      isStreaming: true
    })
    
    const messageIndex = messages.value.length - 1
    
    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value, { stream: true })
      assistantMessage += chunk
      
      // 更新消息内容
      messages.value[messageIndex].content = assistantMessage
      
      // 滚动到底部
      await scrollToBottom()
    }
    
    // 完成流式传输
    messages.value[messageIndex].isStreaming = false
  } catch (error) {
    console.error('Error:', error)
    
    // 尝试获取更详细的错误信息
    let errorMessage = '抱歉，发生了错误。'
    
    if (response && !response.ok) {
      errorMessage += ` 服务器返回: ${response.status} ${response.statusText}`
      try {
        const errorText = await response.text()
        console.error('Error response:', errorText)
        errorMessage += `\n\n详细信息: ${errorText}`
      } catch (e) {
        console.error('Failed to read error response:', e)
      }
    } else {
      errorMessage += ` ${error.message || '请稍后再试。'}`
    }
    
    messages.value.push({
      role: 'assistant',
      content: errorMessage,
      isStreaming: false
    })
  } finally {
    isTyping.value = false
    scrollToBottom()
  }
}

// 监听消息变化，高亮代码
watch(messages, () => {
  nextTick(() => {
    document.querySelectorAll('pre code').forEach((block) => {
      hljs.highlightElement(block)
    })
  })
}, { deep: true })

// 监听输入框变化，自动调整高度
watch(newMessage, () => {
  autoResizeTextarea()
})

onMounted(() => {
  scrollToBottom()
  
  // 配置marked
  marked.setOptions({
    highlight: function(code, lang) {
      const language = hljs.getLanguage(lang) ? lang : 'plaintext'
      return hljs.highlight(code, { language }).value
    },
    langPrefix: 'hljs language-'
  })
})

// 组件卸载时清理资源
onUnmounted(() => {
  if (typingInterval) {
    clearInterval(typingInterval)
    typingInterval = null
  }
})
</script>

<style scoped lang="scss">
.consultant-view {
  min-height: calc(100vh - 64px);
  background-color: var(--bg-color);
  display: flex;
  justify-content: center;
  padding: 1rem;
  
  .container {
    width: 100%;
    max-width: 1000px;
    height: calc(100vh - 100px);
    display: flex;
    flex-direction: column;
  }
  
  .chat-container {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    overflow: hidden;
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .chat-header {
    padding: 1.5rem;
    text-align: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    flex-direction: column;
    align-items: center;
    
    h1 {
      font-size: 1.8rem;
      margin-bottom: 0.5rem;
      background: linear-gradient(45deg, #007CF0, #00DFD8);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    
    p {
      color: #888;
      font-size: 1rem;
      margin-bottom: 1rem;
    }
    
    .new-chat-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0.5rem 1rem;
      background-color: rgba(0, 124, 240, 0.1);
      color: #007CF0;
      border: 1px solid rgba(0, 124, 240, 0.2);
      border-radius: 9999px;
      font-size: 0.9rem;
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover {
        background-color: rgba(0, 124, 240, 0.2);
      }
      
      svg {
        width: 1.2rem;
        height: 1.2rem;
        margin-right: 0.3rem;
      }
      
      .dark & {
        background-color: rgba(0, 124, 240, 0.2);
      }
    }
  }
  
  .chat-messages {
    flex-grow: 1;
    overflow-y: auto;
    padding: 1rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
    
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-thumb {
      background-color: rgba(255, 255, 255, 0.2);
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-track {
      background-color: transparent;
    }
  }
  
  .message {
    max-width: 85%;
    align-self: flex-start;
    
    &.user {
      align-self: flex-end;
      
      .message-content {
        flex-direction: row-reverse;
        
        .text {
          background-color: #007CF0;
          color: white;
          border-radius: 18px 18px 4px 18px;
        }
        
        .avatar {
          margin-left: 8px;
          margin-right: 0;
        }
      }
    }
  }
  
  .message-content {
    display: flex;
    align-items: flex-start;
    
    .avatar {
      width: 38px;
      height: 38px;
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 8px;
      background-color: rgba(255, 255, 255, 0.1);
      color: #888;
      
      svg {
        width: 24px;
        height: 24px;
      }
      
      &.user {
        background-color: #007CF0;
        color: white;
      }
    }
    
    .text {
      padding: 12px 16px;
      border-radius: 18px 18px 18px 4px;
      background-color: rgba(255, 255, 255, 0.07);
      color: var(--text-color);
      line-height: 1.5;
      font-size: 1rem;
      
      :deep(p) {
        margin: 0 0 0.75rem 0;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
      
      :deep(pre) {
        margin: 0.75rem 0;
        padding: 1rem;
        border-radius: 8px;
        overflow-x: auto;
        background-color: #1e1e1e;
      }
      
      :deep(code) {
        font-family: 'Fira Code', monospace;
        font-size: 0.9rem;
      }
      
      :deep(ul), :deep(ol) {
        margin: 0.75rem 0;
        padding-left: 1.5rem;
      }
    }
  }
  
  .typing-cursor {
    display: inline-block;
    width: 2px;
    height: 16px;
    background: currentColor;
    margin-left: 2px;
    animation: blink 1s step-end infinite;
  }

  @keyframes blink {
    from, to {
      opacity: 1;
    }
    50% {
      opacity: 0;
    }
  }

  .typing-indicator {
    display: flex;
    align-items: center;
    padding: 0.75rem;
    border-radius: 18px 18px 18px 4px;
    background-color: rgba(255, 255, 255, 0.07);
    
    span {
      display: inline-block;
      width: 8px;
      height: 8px;
      margin: 0 2px;
      background-color: #888;
      border-radius: 50%;
      animation: bounce 1.4s infinite ease-in-out;
      
      &:nth-child(1) {
        animation-delay: -0.32s;
      }
      
      &:nth-child(2) {
        animation-delay: -0.16s;
      }
    }
  }
  
  .chat-input {
    padding: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    gap: 0.5rem;
    
    textarea {
      flex-grow: 1;
      background-color: rgba(255, 255, 255, 0.07);
      border: none;
      border-radius: 24px;
      padding: 12px 16px;
      color: var(--text-color);
      font-size: 1rem;
      resize: none;
      max-height: 150px;
      outline: none;
      transition: background-color 0.3s;
      
      &:focus {
        background-color: rgba(255, 255, 255, 0.1);
      }
      
      &::placeholder {
        color: #888;
      }
    }
    
    button {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      border: none;
      background-color: #007CF0;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: transform 0.2s, background-color 0.3s;
      
      &:hover {
        background-color: #0070d8;
        transform: scale(1.05);
      }
      
      &:disabled {
        background-color: rgba(255, 255, 255, 0.2);
        cursor: not-allowed;
      }
      
      svg {
        width: 24px;
        height: 24px;
      }
    }
  }
}

@keyframes bounce {
  0%, 80%, 100% { 
    transform: scale(0);
  } 
  40% { 
    transform: scale(1);
  }
}

@media (max-width: 768px) {
  .consultant-view {
    padding: 0.5rem;
    
    .container {
      height: calc(100vh - 80px);
    }
    
    .chat-header {
      padding: 1rem;
      
      h1 {
        font-size: 1.5rem;
      }
    }
    
    .message {
      max-width: 95%;
    }
  }
}
</style> 