<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h2>登录</h2>
          <p>欢迎回到个人博客系统</p>
        </div>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              size="large"
              prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-footer">
          <p>
            还没有账号？
            <el-link type="primary" @click="$router.push('/register')">
              立即注册
            </el-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const success = await userStore.login(loginForm)
    if (success) {
      router.push('/')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 登录页面样式 */
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.login-page::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-container {
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 1;
}

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 32px 80px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px 24px 0 0;
}

/* 登录标题 */
.login-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.login-header h2 {
  margin: 0 0 12px 0;
  font-size: 2rem;
  color: #333;
  font-weight: 800;
  position: relative;
}

.login-header h2::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.login-header p {
  margin: 0;
  color: #666;
  font-size: 1rem;
  font-weight: 400;
}

/* 登录表单 */
.login-form {
  margin-bottom: 32px;
}

.login-form .el-form-item {
  margin-bottom: 24px;
}

.login-form .el-input {
  border-radius: 12px;
}

.login-form .el-input__wrapper {
  background: rgba(255, 255, 255, 0.8);
  border: 2px solid rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  padding: 16px 20px;
  transition: all 0.3s ease;
}

.login-form .el-input__wrapper:hover {
  border-color: rgba(102, 126, 234, 0.3);
  background: rgba(255, 255, 255, 0.9);
}

.login-form .el-input.is-focus .el-input__wrapper {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
  background: rgba(255, 255, 255, 1);
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-size: 1.1rem;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.5);
}

.login-button:active {
  transform: translateY(0);
}

/* 登录页脚 */
.login-footer {
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
}

.login-footer p {
  margin: 0;
  color: #666;
  font-size: 0.95rem;
}

.login-footer .el-link {
  font-weight: 600;
  text-decoration: none;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
    border-radius: 16px;
    margin: 0 16px;
  }
  
  .login-header h2 {
    font-size: 1.75rem;
  }
  
  .login-button {
    height: 48px;
    font-size: 1rem;
  }
}

/* 错误提示样式 */
.login-form .el-form-item__error {
  color: #f56565;
  font-size: 0.875rem;
  margin-top: 8px;
  padding-left: 4px;
}

/* 加载状态 */
.login-button.is-loading {
  pointer-events: none;
}

/* 动画效果 */
.login-card {
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>