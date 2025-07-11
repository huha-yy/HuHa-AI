import { ref } from 'vue';
import axios from 'axios'; // 导入 axios

const LOCAL_STORAGE_USER_KEY = 'currentUser';
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://10.159.243.185:8080';

// 使用 ref 存储当前用户，使其具有响应性
const currentUser = ref(loadUserFromLocalStorage());

function loadUserFromLocalStorage() {
    const userJson = localStorage.getItem(LOCAL_STORAGE_USER_KEY);
    try {
        return userJson ? JSON.parse(userJson) : null;
    } catch (e) {
        console.error("Failed to parse user from localStorage:", e);
        return null;
    }
}

// 简单的占位符头像生成方法 (仍然用于前端展示)
function generatePlaceholderAvatar(username) {
    if (username == null || username.length < 2) {
        return username;
    } else {
        return username.substring(username.length - 2);
    }
}

class AuthService {
    async login(username) {
        try {
            // 调用后端登录接口
            const response = await axios.post(`${BASE_URL}/api/user/login`, { username: username });
            const user = response.data; // 后端返回的用户对象

            // 将从后端获取的用户信息存储到 localStorage 和响应式 ref
            localStorage.setItem(LOCAL_STORAGE_USER_KEY, JSON.stringify(user));
            currentUser.value = user;
            console.log("User logged in (backend data):", user);
            return user;
        } catch (error) {
            console.error("Login failed:", error);
            // 登录失败时清除本地存储和用户状态
            this.logout();
            throw error; // 抛出错误以便调用方处理
        }
    }

    logout() {
        localStorage.removeItem(LOCAL_STORAGE_USER_KEY);
        currentUser.value = null; // 更新响应式 ref
        console.log("User logged out");
        // 如果需要通知后端用户下线，可以在这里添加 API 调用
    }

    isLoggedIn() {
        return currentUser.value !== null; // 从响应式 ref 获取状态
    }

    getCurrentUser() {
        return currentUser.value; // 从响应式 ref 获取用户对象
    }
}

export const authService = new AuthService(); 