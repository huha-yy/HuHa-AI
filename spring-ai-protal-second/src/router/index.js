import { createRouter, createWebHistory } from 'vue-router'
import GameChat from '../views/GameChat.vue'
import ChatRoom from '../components/ChatRoom.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/ai-chat',
    name: 'AIChat',
    component: () => import('../views/AIChat.vue')
  },
  {
    path: '/comfort-simulator',
    name: 'ComfortSimulator',
    component: () => import('../views/GameChat.vue'),
    alias: '/game'
  },
  {
    path: '/customer-service',
    name: 'CustomerService',
    component: () => import('../views/CustomerService.vue')
  },
  {
    path: '/chat-pdf',
    name: 'ChatPDF',
    component: () => import('../views/ChatPDF.vue')
  },
  {
    path: '/chat',
    name: 'ChatList',
    component: () => import('../views/ChatList.vue')
  },
  {
    path: '/chat/:roomId',
    name: 'ChatRoom',
    component: ChatRoom,
    props: true
  },
  {
    path: '/consultant',
    name: 'CollegeAdvisor',
    component: () => import('../views/ConsultantView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 