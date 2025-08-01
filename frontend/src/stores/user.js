import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const nickname = computed(() => userInfo.value?.nickname || '')
  const avatar = computed(() => userInfo.value?.avatar || '')

  // 方法
  const login = async (loginData) => {
    try {
      const response = await userApi.login(loginData)
      const { data } = response
      
      // 保存token和用户信息
      token.value = data
      localStorage.setItem('token', data)
      
      // 获取用户信息
      await getUserInfo()
      
      ElMessage.success('登录成功')
      return true
    } catch (error) {
      ElMessage.error(error.message || '登录失败')
      return false
    }
  }

  const register = async (registerData) => {
    try {
      await userApi.register(registerData)
      ElMessage.success('注册成功，请登录')
      return true
    } catch (error) {
      ElMessage.error(error.message || '注册失败')
      return false
    }
  }

  const getUserInfo = async () => {
    try {
      const response = await userApi.getUserInfo()
      const { data } = response
      
      userInfo.value = data
      localStorage.setItem('userInfo', JSON.stringify(data))
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  const updateUserInfo = async (userData) => {
    try {
      await userApi.updateUserInfo(userData)
      await getUserInfo() // 重新获取用户信息
      ElMessage.success('更新成功')
      return true
    } catch (error) {
      ElMessage.error(error.message || '更新失败')
      return false
    }
  }

  const changePassword = async (passwordData) => {
    try {
      await userApi.changePassword(passwordData)
      ElMessage.success('密码修改成功')
      return true
    } catch (error) {
      ElMessage.error(error.message || '密码修改失败')
      return false
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
  }

  return {
    // 状态
    token,
    userInfo,
    
    // 计算属性
    isLoggedIn,
    username,
    nickname,
    avatar,
    
    // 方法
    login,
    register,
    getUserInfo,
    updateUserInfo,
    changePassword,
    logout
  }
})