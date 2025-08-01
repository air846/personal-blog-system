<template>
  <el-header class="modern-header">
    <div class="container">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo" @click="$router.push('/')">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
            </svg>
          </div>
          <h1 class="gradient-text">个人博客</h1>
        </div>

        <!-- 搜索框 -->
        <div class="search-box">
          <div class="search-wrapper">
            <el-icon class="search-icon"><Search /></el-icon>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索文章、标签、作者..."
              @keyup.enter="handleSearch"
              clearable
              class="search-input"
            />
            <el-button 
              class="search-btn modern-btn" 
              @click="handleSearch"
              :disabled="!searchKeyword.trim()"
            >
              搜索
            </el-button>
          </div>
        </div>

        <!-- 导航菜单 -->
        <div class="nav-menu">
          <nav class="nav-links">
            <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </router-link>
            <router-link 
              v-if="userStore.isLoggedIn" 
              to="/write" 
              class="nav-link" 
              :class="{ active: $route.path.startsWith('/write') }"
            >
              <el-icon><EditPen /></el-icon>
              <span>写文章</span>
            </router-link>
          </nav>
        </div>

        <!-- 用户菜单 -->
        <div class="user-menu">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleUserCommand" class="user-dropdown">
              <div class="user-info">
                <el-avatar :size="36" :src="userStore.avatar" class="user-avatar">
                  {{ userStore.nickname?.charAt(0) || userStore.username?.charAt(0) }}
                </el-avatar>
                <div class="user-details">
                  <span class="username">{{ userStore.nickname || userStore.username }}</span>
                  <span class="user-role">博主</span>
                </div>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="modern-dropdown">
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <div class="auth-buttons">
              <el-button class="login-btn" @click="$router.push('/login')">
                登录
              </el-button>
              <el-button type="primary" class="register-btn" @click="$router.push('/register')">
                注册
              </el-button>
            </div>
          </template>
        </div>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  Search, 
  House, 
  EditPen, 
  User, 
  ArrowDown, 
  SwitchButton 
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')

// 搜索处理
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { keyword: searchKeyword.value.trim() }
    })
  }
}

// 用户菜单命令处理
const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      userStore.logout()
      router.push('/')
      break
  }
}
</script>

<style scoped>
/* 现代化头部样式 */
.modern-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 80px;
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  gap: 32px;
}

/* Logo样式 */
.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 12px;
}

.logo:hover {
  background: rgba(102, 126, 234, 0.05);
  transform: translateY(-1px);
}

.logo-icon {
  width: 32px;
  height: 32px;
  color: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 100%;
  height: 100%;
}

.gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.5px;
}

/* 搜索框样式 */
.search-box {
  flex: 1;
  max-width: 500px;
  margin: 0 32px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 24px;
  padding: 8px 16px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.search-wrapper:hover {
  background: rgba(102, 126, 234, 0.08);
  border-color: rgba(102, 126, 234, 0.2);
}

.search-wrapper:focus-within {
  background: rgba(255, 255, 255, 0.9);
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.search-icon {
  color: #667eea;
  margin-right: 12px;
  font-size: 18px;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
}

.search-input :deep(.el-input__wrapper) {
  background: transparent;
  border: none;
  box-shadow: none;
  padding: 0;
}

.search-btn {
  margin-left: 12px;
  border-radius: 16px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
}

/* 导航菜单样式 */
.nav-menu {
  display: flex;
  align-items: center;
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 12px;
  text-decoration: none;
  color: #666;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.nav-link:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.05);
  transform: translateY(-1px);
}

.nav-link.active {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

/* 用户菜单样式 */
.user-menu {
  display: flex;
  align-items: center;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(102, 126, 234, 0.05);
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: #666;
  line-height: 1.2;
}

.dropdown-icon {
  color: #666;
  font-size: 14px;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 认证按钮样式 */
.auth-buttons {
  display: flex;
  gap: 12px;
}

.login-btn {
  border-radius: 12px;
  padding: 10px 20px;
  font-weight: 500;
  border: 2px solid rgba(102, 126, 234, 0.2);
  color: #667eea;
  background: transparent;
}

.login-btn:hover {
  background: rgba(102, 126, 234, 0.05);
  border-color: #667eea;
}

.register-btn {
  border-radius: 12px;
  padding: 10px 20px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.register-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

/* 下拉菜单样式 */
.modern-dropdown {
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.95);
}

.modern-dropdown .el-dropdown-menu__item {
  padding: 12px 16px;
  border-radius: 8px;
  margin: 4px;
  transition: all 0.3s ease;
}

.modern-dropdown .el-dropdown-menu__item:hover {
  background: rgba(102, 126, 234, 0.05);
  color: #667eea;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    gap: 16px;
  }
  
  .search-box {
    max-width: 200px;
    margin: 0 16px;
  }
  
  .nav-links {
    display: none;
  }
  
  .user-details {
    display: none;
  }
  
  .auth-buttons {
    gap: 8px;
  }
  
  .login-btn,
  .register-btn {
    padding: 8px 16px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .search-box {
    display: none;
  }
  
  .logo h1 {
    font-size: 1.2rem;
  }
}
</style>