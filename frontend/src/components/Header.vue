<template>
  <header class="header">
    <div class="container">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo">
          <router-link to="/" class="logo-link">
            <h1>个人博客</h1>
          </router-link>
        </div>
        
        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文章..."
            @keyup.enter="handleSearch"
            clearable
          >
            <template #append>
              <el-button @click="handleSearch" :icon="Search" />
            </template>
          </el-input>
        </div>
        
        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/write" class="nav-item" v-if="userStore.isLoggedIn">写文章</router-link>
        </nav>
        
        <!-- 用户菜单 -->
        <div class="user-menu">
          <template v-if="!userStore.isLoggedIn">
            <router-link to="/login" class="btn btn-outline">登录</router-link>
            <router-link to="/register" class="btn btn-primary">注册</router-link>
          </template>
          <template v-else>
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :src="userStore.user?.avatar" :size="32">
                  {{ userStore.user?.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="username">{{ userStore.user?.nickname || '用户' }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { Search, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')

// 搜索处理
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      name: 'Search',
      query: { keyword: searchKeyword.value.trim() }
    })
  }
}

// 下拉菜单命令处理
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      userStore.logout()
      break
  }
}
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  height: 70px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
  gap: 20px;
}

.logo {
  flex-shrink: 0;
}

.logo-link {
  text-decoration: none;
  color: #333;
}

.logo h1 {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin: 0;
}

.search-box {
  flex: 1;
  max-width: 400px;
  margin: 0 20px;
}

.nav-menu {
  display: flex;
  gap: 20px;
  flex-shrink: 0;
}

.nav-item {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #409eff;
  background-color: #ecf5ff;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.btn-outline {
  color: #409eff;
  border-color: #409eff;
}

.btn-outline:hover {
  background-color: #409eff;
  color: white;
}

.btn-primary {
  background-color: #409eff;
  color: white;
}

.btn-primary:hover {
  background-color: #66b1ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    height: 60px;
  }
  
  .header-content {
    height: 60px;
    gap: 10px;
  }
  
  .search-box {
    max-width: 200px;
    margin: 0 10px;
  }
  
  .nav-menu {
    display: none;
  }
  
  .logo h1 {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .search-box {
    display: none;
  }
  
  .user-menu .btn {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style>