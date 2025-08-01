import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '../api/user'
import { ElMessage } from 'element-plus'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  
  const isLoggedIn = computed(() => !!token.value)
  
  // 登录
  const login = async (username, password) => {
    try {
      const response = await userApi.login({ username, password })
      if (response.code === 200) {
        token.value = response.data
        localStorage.setItem('token', response.data)
        await getUserInfo()
        ElMessage.success('登录成功')
        router.push('/')
        return true
      } else {
        ElMessage.error(response.message || '登录失败')
        return false
      }
    } catch (error) {
      ElMessage.error('登录失败，请检查网络连接')
      return false
    }
  }
  
  // 注册
  const register = async (userData) => {
    try {
      const response = await userApi.register(userData)
      if (response.code === 200) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
        return true
      } else {
        ElMessage.error(response.message || '注册失败')
        return false
      }
    } catch (error) {
      ElMessage.error('注册失败，请检查网络连接')
      return false
    }
  }
  
  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const response = await userApi.getUserInfo()
      if (response.code === 200) {
        user.value = response.data
        return true
      } else {
        logout()
        return false
      }
    } catch (error) {
      logout()
      return false
    }
  }
  
  // 更新用户信息
  const updateUserInfo = async (userData) => {
    try {
      const response = await userApi.updateUserInfo(userData)
      if (response.code === 200) {
        await getUserInfo()
        ElMessage.success('更新成功')
        return true
      } else {
        ElMessage.error(response.message || '更新失败')
        return false
      }
    } catch (error) {
      ElMessage.error('更新失败，请检查网络连接')
      return false
    }
  }
  
  // 退出登录
  const logout = () => {
    user.value = null
    token.value = ''
    localStorage.removeItem('token')
    router.push('/login')
    ElMessage.success('已退出登录')
  }
  
  // 初始化时获取用户信息
  if (token.value) {
    getUserInfo()
  }
  
  return {
    user,
    token,
    isLoggedIn,
    login,
    register,
    getUserInfo,
    updateUserInfo,
    logout
  }
})